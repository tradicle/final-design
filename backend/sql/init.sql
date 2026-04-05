CREATE DATABASE IF NOT EXISTS animal_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE animal_db;

-- Drop tables if they exist to reset data
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS animal_location;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS clue;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS donation;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS knowledge;

-- System User Table
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(100) NOT NULL COMMENT 'Password',
    email VARCHAR(100) COMMENT 'Email',
    role VARCHAR(20) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
    avatar VARCHAR(255) COMMENT 'User Avatar',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System User Table';

-- Animal Profile Table
CREATE TABLE animal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    animal_no VARCHAR(64) UNIQUE COMMENT 'Animal Number',
    name VARCHAR(50) NOT NULL COMMENT 'Pet Name',
    category VARCHAR(20) DEFAULT 'DOG' COMMENT 'Category: CAT, DOG, OTHER',
    sex VARCHAR(20) DEFAULT 'MALE' COMMENT 'Sex: MALE, FEMALE',
    body_size VARCHAR(20) DEFAULT 'MEDIUM' COMMENT 'Body Size: SMALL, MEDIUM, LARGE',
    age INT COMMENT 'Pet Age (Years)',
    avatar VARCHAR(255) COMMENT 'Avatar URL',
    is_sterilized TINYINT(1) DEFAULT 0 COMMENT 'Is Sterilized: 0-No, 1-Yes',
    activity_scope VARCHAR(255) COMMENT 'Activity Range',
    status TINYINT(1) DEFAULT 1 COMMENT 'Status: 1-Available, 0-Adopted',
    description TEXT COMMENT 'Description',
    detail_content LONGTEXT COMMENT 'Rich Text Detail',
    latitude DECIMAL(10, 6) COMMENT 'Current Latitude',
    longitude DECIMAL(10, 6) COMMENT 'Current Longitude',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Animal Profile Table';

-- Animal Location History Table (For Heatmap/Route)
CREATE TABLE animal_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    animal_id BIGINT NOT NULL COMMENT 'Animal ID',
    latitude DECIMAL(10, 6) NOT NULL COMMENT 'Latitude',
    longitude DECIMAL(10, 6) NOT NULL COMMENT 'Longitude',
    record_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Record Time',
    FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Animal Location History';

-- Clue Table (Stray Animal Reports)
CREATE TABLE clue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    title VARCHAR(100) NOT NULL COMMENT 'Title',
    content TEXT COMMENT 'Content',
    location VARCHAR(255) COMMENT 'Location',
    contact VARCHAR(50) COMMENT 'Contact Info',
    image VARCHAR(255) COMMENT 'Image URL',
    status TINYINT(1) DEFAULT 0 COMMENT 'Status: 0-Pending, 1-Processed',
    user_id BIGINT COMMENT 'Reporter User ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Clue Report Table';

-- News Table (Events/Activities)
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    title VARCHAR(200) NOT NULL COMMENT 'Title',
    summary VARCHAR(500) COMMENT 'Summary',
    content TEXT COMMENT 'Content (HTML/Markdown)',
    cover_image VARCHAR(255) COMMENT 'Cover Image URL',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Publish Time',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='News and Events';

-- Donation Table (Placeholder for future)
CREATE TABLE donation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    donor_name VARCHAR(50) COMMENT 'Donor Name',
    amount DECIMAL(10, 2) NOT NULL COMMENT 'Amount',
    message VARCHAR(255) COMMENT 'Message',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Donation Records';

CREATE TABLE knowledge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    title VARCHAR(120) NOT NULL COMMENT 'Title',
    content TEXT COMMENT 'Content',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Knowledge';

-- Community Post Table
CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    title VARCHAR(100) NOT NULL COMMENT 'Title',
    content TEXT NOT NULL COMMENT 'Content',
    images TEXT COMMENT 'Images JSON',
    location VARCHAR(255) COMMENT 'Location Name',
    latitude DECIMAL(10, 6) COMMENT 'Latitude',
    longitude DECIMAL(10, 6) COMMENT 'Longitude',
    status TINYINT(1) DEFAULT 1 COMMENT 'Status: 0-Hidden, 1-Normal',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Community Posts';

-- Community Comment Table
CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    post_id BIGINT NOT NULL COMMENT 'Post ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    content TEXT NOT NULL COMMENT 'Content',
    image VARCHAR(255) COMMENT 'Comment Image URL',
    parent_id BIGINT COMMENT 'Parent Comment ID',
    status TINYINT(1) DEFAULT 1 COMMENT 'Status: 0-Hidden, 1-Normal',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Community Comments';

-- Insert Sample Users
INSERT INTO sys_user (username, password, role, avatar) VALUES 
('admin', 'admin123', 'ADMIN', 'https://api.dicebear.com/9.x/avataaars/svg?seed=admin'),
('alice', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=alice'),
('bob', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=bob'),
('charlie', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=charlie'),
('david', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=david'),
('eve', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=eve');

INSERT INTO animal (animal_no, name, category, sex, body_size, age, avatar, is_sterilized, activity_scope, status, description, detail_content, latitude, longitude) VALUES
('SZ753', 'lucky', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958d0bef1a17.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '2025年11月救助的小黄白猫，性格活泼，适合家庭领养。', '<p>2025年11月救助的小黄白猫，状态稳定，等待领养。</p>', 22.543096, 114.057865),
('SZ752', '小莎', 'CAT', 'FEMALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958cf541a948.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小莎为三花幼猫，健康活泼，适合室内饲养。', '<p>小莎来自深圳湾附近救助点，当前状态良好，可预约领养。</p>', 22.543096, 114.057865),
('SZ751', '小加', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958ce2d82c1e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小加是狸花幼猫，亲人，适应力较好。', '<p>小加由志愿者救助后进入领养中心，目前疫苗驱虫按计划进行。</p>', 22.543096, 114.057865),
('SZ750', '小桦', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958bf9831783.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小桦为狸花幼猫，活泼好动，喜欢互动。', '<p>小桦与同批幼猫一起救助，健康状况稳定，等待新家庭。</p>', 22.543096, 114.057865),
('SZ749', '瑶瑶', 'CAT', 'FEMALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/68634a869c79e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '瑶瑶为橘猫幼猫，性格温顺。', '<p>瑶瑶于2025年救助入站，已完成基础体检，可申请领养。</p>', 22.543096, 114.057865),
('SZ719', '沙皮', 'DOG', 'MALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/667d1c46d11c1.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '沙皮为串串犬，亲人，适合家庭陪伴。', '<p>沙皮已完成基础免疫，具备领养条件。</p>', 22.543096, 114.057865),
('SZ718', '黑豆', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/66485c0c007cf.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '黑豆为母串串，性格稳定。', '<p>黑豆目前状态良好，等待负责任领养家庭。</p>', 22.543096, 114.057865),
('SZ717', '五百', 'DOG', 'MALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/6648590bc3772.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '五百为公串串，活泼外向。', '<p>五百亲人，适合有遛狗条件的家庭。</p>', 22.543096, 114.057865),
('SZ711', '元宝', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/664704b0ea083.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '元宝为母串串，适合城市家庭。', '<p>元宝已在中心观察一段时间，状态稳定可领养。</p>', 22.543096, 114.057865),
('SZ710', '六斤', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/664704e1b359f.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '六斤体型适中，亲人。', '<p>六斤适应能力较好，建议有稳定作息的家庭领养。</p>', 22.543096, 114.057865);

-- Insert Sample Location History
INSERT INTO animal_location (animal_id, latitude, longitude) VALUES 
(1, 39.9042, 116.4074), (1, 39.9050, 116.4080), (1, 39.9060, 116.4090), (1, 39.9030, 116.4060);
INSERT INTO animal_location (animal_id, latitude, longitude) VALUES 
(2, 39.915, 116.404), (2, 39.916, 116.405), (2, 39.914, 116.403);

-- Insert Sample Clues
INSERT INTO clue (title, content, location, contact, status) VALUES 
('发现流浪猫', '在公园长椅下发现一只受伤的小猫。', '人民公园', '13800138000', 0),
('疑似走失金毛', '在超市门口看到一只金毛，没有主人。', '家乐福门口', '13900139000', 0);

-- Insert Sample News
INSERT INTO news (title, summary, content, cover_image) VALUES 
('爱心义卖！2026《汪喵漫游世界》公益台历现货发售', '2026汪喵公益台历义卖开始啦！我们今年的任务是卖出2000本台历，凑够冬季取暖费！', '详细内容...', 'https://picsum.photos/800/400?random=1'),
('给毛孩子的爱与仪式感！生日大餐吃了，年夜饭还会远吗？', '在汪汪喵呜孤儿院，每个曾漂泊的毛孩子，都能拥有属于自己的“仪式感时刻”。', '详细内容...', 'https://picsum.photos/800/400?random=2'),
('HI，亲们，一起帮流浪猫拼好窝吗？宫猫同款哦！', '立秋了，冬天还会远吗？汪汪喵呜孤儿院流浪猫户外暖冬猫屋开始筹备了！', '详细内容...', 'https://picsum.photos/800/400?random=3');

-- Insert Sample Posts
INSERT INTO post (user_id, title, content, location, latitude, longitude) VALUES 
(2, '大家周末有空一起去喂猫吗？', '这周末打算去奥森公园喂流浪猫，有没有一起的？', '奥林匹克森林公园', 40.007, 116.397),
(3, '晒晒我家领养的狗狗', '太可爱了，感谢平台让我遇到了它！', '朝阳区', 39.92, 116.44);

-- Insert Sample Comments
INSERT INTO comment (post_id, user_id, content) VALUES 
(1, 3, '我有空，算我一个！'),
(1, 4, '几点集合呀？');

INSERT INTO knowledge (title, content, sort_order) VALUES
('工作时间', '周一至周日 早上9:00-12:00 下午1:30-6:00', 1),
('联系方式', '座机：0755-86035169 | 苏先生：137 1711 2376 | 王先生：137 9449 7660', 2),
('地址一', '深圳市南山区沙河街道睿印商城B2层 下沉广场 喵喵领养小屋（地铁红树湾南E出口旁）', 3),
('地址二', '深圳地铁2号线—湾厦站A出口—北京银行后海支行附近（猫）', 4),
('地址三', '龙岗区大芬百门前工业区2栋7楼宠物领养之家（狗）', 5),
('Q群', '26563144', 6);
