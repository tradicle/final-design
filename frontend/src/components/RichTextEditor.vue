<script setup lang="ts">
import { ref, watch, onBeforeUnmount } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import { uploadFile } from '../api/file'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement>()

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
    const res = await uploadFile(file)
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
</script>

<template>
  <div class="rich-text-editor">
    <div class="toolbar" v-if="editor">
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
  gap: 4px;
  padding: 6px 8px;
  background: #fafafa;
  border-bottom: 1px solid var(--el-border-color, #dcdfe6);
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
}

.toolbar-btn:hover:not(:disabled) {
  background: #e6e6e6;
}

.toolbar-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
</style>
