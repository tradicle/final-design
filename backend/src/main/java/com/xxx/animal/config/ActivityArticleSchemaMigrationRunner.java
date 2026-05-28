package com.xxx.animal.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActivityArticleSchemaMigrationRunner implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public ActivityArticleSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS activity_article (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
                    title VARCHAR(200) NOT NULL COMMENT 'Title',
                    summary VARCHAR(500) COMMENT 'Summary',
                    content LONGTEXT COMMENT 'HTML Content',
                    cover_image VARCHAR(500) COMMENT 'Cover Image URL',
                    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Publish Time',
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Love Activities'
                """);

        Integer exists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM activity_article", Integer.class);
        if (exists != null && exists > 0) {
            return;
        }

        insertSeed(
                "爱心义卖！2026《汪喵漫游世界》公益台历现货发售",
                "2026 公益台历正式发售。我们把曾被救助的猫猫狗狗画进温暖的旅行场景里，希望用一本台历连接更多善意，也为冬季照护和日常救助筹集稳定支持。",
                """
                        <h3>活动介绍</h3>
                        <p>毛茸茸之家发起了 2026 年公益台历义卖活动。台历中的主角都来自真实救助故事，我们希望借由插画与文字，让更多人看见流浪动物曾经历的漂泊、伤病与重获新生。</p>
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/214407908261635395.jpeg" alt="公益台历"></figure>
                        <p>这次义卖所得将在扣除制作成本后，全部用于冬季取暖、医疗护理、猫狗口粮与领养中心的日常支出。每一位购买者，都是这些毛孩子未来生活的一部分守护者。</p>
                        <h3>活动意义</h3>
                        <p>我们想把“领养代替购买”“科学救助”“长期陪伴”这些理念，做成能被带回家的实物。一本台历不是终点，而是让更多人持续关注生命议题的开始。</p>
                        """,
                "https://www.pohome.cn/upload/img/blog/feature/1420.jpeg"
        );
        insertSeed(
                "给毛孩子的爱与仪式感！生日大餐吃了，年夜饭还会远吗？",
                "每年我们都会为领养中心的毛孩子们准备集体生日。生日照、生日餐和陪伴，对它们来说不仅是热闹的一天，更是被认真对待、被温柔记住的证明。",
                """
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/215265420698199510.jpeg" alt="生日活动"></figure>
                        <p>今年的集体生日活动依旧延续了“让每个孩子都拥有仪式感”的主题。我们为狗狗和猫咪准备了拍照背景、围兜、生日餐和互动时间，希望它们在镜头里留下真正放松和快乐的模样。</p>
                        <p>这些毛孩子很多都曾经历被遗弃、受伤和长期流浪。如今能围坐在一起等待一顿热腾腾的生日餐，本身就是救助工作最温柔的成果之一。</p>
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/215266465766712809.jpeg" alt="生日餐"></figure>
                        <p>我们也希望通过这场活动，让更多人看见流浪动物并不只是“被同情的对象”，它们也值得庆祝、值得拥有快乐，值得被当成家人一样认真对待。</p>
                        """,
                "https://www.pohome.cn/upload/img/blog/feature/1421.jpeg"
        );
        insertSeed(
                "HI，亲们，一起帮流浪猫拼好窝吗？宫猫同款哦！",
                "暖冬猫屋项目再次启动。除了面向群护点继续支持外，我们也尝试开放共创认领方式，让更多人可以一起为户外流浪猫准备一个结实、保温、安全的冬天小家。",
                """
                        <p>立秋之后，户外流浪猫的越冬准备就要提前开始。毛茸茸之家连续多年制作暖冬猫屋，今年希望通过更开放的方式，让更多爱心人士一起参与到“拼好窝”行动中来。</p>
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/212546519489848442.jpeg" alt="暖冬猫屋"></figure>
                        <p>猫屋采用保温、防潮和耐用的结构设计，目标是在寒冷季节为流浪猫提供可遮风避雨的临时庇护点。我们会优先发放给有稳定照护的群护点和需要帮助的救助伙伴。</p>
                        <p>如果你也希望为社区里的小流浪做点什么，这项活动就是非常直接、也非常有温度的一种参与方式。</p>
                        """,
                "https://www.pohome.cn/upload/img/blog/feature/1419.jpeg"
        );
        insertSeed(
                "滚蛋吧，肿瘤君！帮“开心”撕账单",
                "13 岁的萨摩耶“开心”被查出脾脏异常，需要尽快手术和后续病理检查。这篇活动记录主要用于说明它的治疗进展，以及专项支持的用途。",
                """
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/209986729393791708.jpeg" alt="开心"></figure>
                        <p>“开心”是一只 13 岁的萨摩耶。年后我们发现它行动越来越吃力，进一步检查后确认需要尽快手术处理脾脏问题，否则可能面临突发性的大出血风险。</p>
                        <p>对于老年犬来说，每一次治疗决定都不轻松。我们选择尽全力争取，希望它还能继续安稳地生活下去，也把诊疗过程和费用压力如实向大家说明。</p>
                        <figure><img src="https://www.pohome.cn/upload/img/blog/content/209986957118022368.jpeg" alt="治疗过程"></figure>
                        <p>这篇内容既是求助说明，也是治疗记录。感谢每一位愿意伸手帮忙的人，让“开心”有机会继续走向一个更好的恢复阶段。</p>
                        """,
                "https://www.pohome.cn/upload/img/blog/feature/1413.jpeg"
        );
    }

    private void insertSeed(String title, String summary, String content, String coverImage) {
        jdbcTemplate.update("""
                INSERT INTO activity_article (title, summary, content, cover_image)
                VALUES (?, ?, ?, ?)
                """, title, summary, content, coverImage);
    }
}
