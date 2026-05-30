<script setup lang="ts">
import { ref, watch, onBeforeUnmount, onMounted } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import { uploadFile } from '../api/file'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue: string
  uploadFolder: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement>()
const headingMenuVisible = ref(false)
const headingDropdownRef = ref<HTMLElement>()

function onDocumentClick(e: MouseEvent) {
  if (headingDropdownRef.value && !headingDropdownRef.value.contains(e.target as Node)) {
    headingMenuVisible.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick)
  editor.value?.destroy()
})

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit,
    Image.configure({
      inline: false,
      allowBase64: false,
    }),
  ],
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML())
  },
})

watch(
  () => props.modelValue,
  (value) => {
    if (editor.value && editor.value.getHTML() !== value) {
      editor.value.commands.setContent(value, { emitUpdate: false })
    }
  }
)

onBeforeUnmount(() => {
  editor.value?.destroy()
})

function triggerImageUpload() {
  fileInputRef.value?.click()
}

async function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  uploading.value = true
  try {
    const res = await uploadFile(file, props.uploadFolder)
    if (res.code === 0 && res.data) {
      editor.value?.chain().focus().setImage({ src: res.data }).run()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } finally {
    uploading.value = false
    input.value = ''
  }
}

function toggleHeading(level: 2 | 3) {
  const isActive = editor.value?.isActive('heading', { level })
  if (isActive) {
    editor.value?.chain().focus().setParagraph().run()
  } else {
    editor.value?.chain().focus().toggleHeading({ level }).run()
  }
  headingMenuVisible.value = false
}
</script>

<template>
  <div class="rich-text-editor" v-if="editor">
    <div class="toolbar">
      <button
        type="button"
        class="toolbar-btn"
        :class="{ active: editor.isActive('bold') }"
        title="加粗"
        @click="editor.chain().focus().toggleBold().run()"
      >
        <b>B</b>
      </button>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ active: editor.isActive('italic') }"
        title="斜体"
        @click="editor.chain().focus().toggleItalic().run()"
      >
        <i>I</i>
      </button>
      <div class="heading-dropdown" ref="headingDropdownRef">
        <button
          type="button"
          class="toolbar-btn"
          :class="{ active: editor.isActive('heading') }"
          title="标题"
          @click.stop="headingMenuVisible = !headingMenuVisible"
        >
          H<span class="dropdown-arrow">▼</span>
        </button>
        <div class="heading-menu" v-if="headingMenuVisible">
          <button
            type="button"
            class="heading-option"
            :class="{ active: editor.isActive('heading', { level: 2 }) }"
            @click="toggleHeading(2)"
          >
            二级标题
          </button>
          <button
            type="button"
            class="heading-option"
            :class="{ active: editor.isActive('heading', { level: 3 }) }"
            @click="toggleHeading(3)"
          >
            三级标题
          </button>
        </div>
      </div>
      <button
        type="button"
        class="toolbar-btn"
        :class="{ active: editor.isActive('bulletList') }"
        title="无序列表"
        @click="editor.chain().focus().toggleBulletList().run()"
      >
        <span class="list-icon">•≡</span>
      </button>
      <span class="toolbar-separator"></span>
      <button
        type="button"
        class="toolbar-btn"
        :disabled="uploading"
        title="插入图片"
        @click="triggerImageUpload"
      >
        <el-icon><Picture /></el-icon>
        <span>{{ uploading ? '上传中...' : '图片' }}</span>
      </button>
      <input
        ref="fileInputRef"
        type="file"
        accept="image/*"
        style="display: none"
        @change="onFileChange"
      />
    </div>
    <EditorContent :editor="editor" class="editor-body" />
  </div>
</template>

<style scoped>
.rich-text-editor {
  border: 1px solid var(--el-border-color, #dcdfe6);
  border-radius: 6px;
  overflow: hidden;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  background: #fafafa;
  border-bottom: 1px solid var(--el-border-color, #dcdfe6);
  flex-wrap: wrap;
}

.toolbar-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: transparent;
  color: #555;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  min-width: 32px;
  justify-content: center;
}

.toolbar-btn:hover:not(:disabled) {
  background: #e6e6e6;
}

.toolbar-btn.active {
  background: #e0e0e0;
  border-color: #bbb;
}

.toolbar-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.toolbar-separator {
  display: inline-block;
  width: 1px;
  height: 20px;
  background: #ddd;
  margin: 0 4px;
}

.heading-dropdown {
  position: relative;
}

.dropdown-arrow {
  font-size: 9px;
  margin-left: 1px;
}

.heading-menu {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  min-width: 110px;
  overflow: hidden;
}

.heading-option {
  display: block;
  width: 100%;
  padding: 8px 14px;
  border: none;
  background: transparent;
  font-size: 13px;
  text-align: left;
  cursor: pointer;
  transition: background 0.1s;
}

.heading-option:hover {
  background: #f0f0f0;
}

.heading-option.active {
  background: #e8e8ff;
  color: #3355cc;
}

.list-icon {
  font-size: 15px;
  letter-spacing: -2px;
}

.editor-body {
  min-height: 280px;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1.7;
  color: #333;
  outline: none;
}

.editor-body :deep(.ProseMirror) {
  min-height: 260px;
  outline: none;
}

.editor-body :deep(.ProseMirror p) {
  margin: 0 0 8px;
}

.editor-body :deep(.ProseMirror img) {
  max-width: 100%;
  border-radius: 6px;
}

.editor-body :deep(.ProseMirror h1),
.editor-body :deep(.ProseMirror h2),
.editor-body :deep(.ProseMirror h3) {
  margin: 12px 0 8px;
  line-height: 1.3;
}

.editor-body :deep(.ProseMirror ul) {
  padding-left: 24px;
  margin: 8px 0;
}

.editor-body :deep(.ProseMirror li) {
  margin: 4px 0;
}
</style>
