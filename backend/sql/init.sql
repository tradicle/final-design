CREATE DATABASE IF NOT EXISTS animal_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE animal_db;

-- Drop tables if they exist to reset data
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS default_avatar;
DROP TABLE IF EXISTS animal_location;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS donation;
DROP TABLE IF EXISTS donation_record;
DROP TABLE IF EXISTS donation_claim;
DROP TABLE IF EXISTS urgent_need;
DROP TABLE IF EXISTS weekly_update;
DROP TABLE IF EXISTS transparency_record;
DROP TABLE IF EXISTS system_config;
DROP TABLE IF EXISTS adoption_application;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS knowledge;

-- System User Table
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    nickname VARCHAR(50) COMMENT 'Nickname',
    password VARCHAR(100) NOT NULL COMMENT 'Password',
    email VARCHAR(100) COMMENT 'Email',
    role VARCHAR(20) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
    avatar LONGTEXT COMMENT 'User Avatar',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System User Table';

CREATE TABLE default_avatar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    name VARCHAR(50) NOT NULL COMMENT 'Avatar Name',
    image_data LONGTEXT NOT NULL COMMENT 'Avatar Image Data URI',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Default Avatar Table';

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
    location VARCHAR(255) COMMENT 'Found Location Name',
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

CREATE TABLE donation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    date VARCHAR(32) COMMENT 'Display Date',
    donor VARCHAR(120) COMMENT 'Donor Name',
    item VARCHAR(255) COMMENT 'Donation Item',
    quantity VARCHAR(40) COMMENT 'Quantity',
    unit VARCHAR(40) COMMENT 'Unit',
    remark VARCHAR(200) COMMENT 'Remark',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Donation Public Records';

CREATE TABLE donation_claim (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    need_name VARCHAR(120) COMMENT 'Need Name',
    need_gap VARCHAR(80) COMMENT 'Current Gap',
    quantity VARCHAR(80) COMMENT 'Claim Quantity',
    contact_name VARCHAR(80) COMMENT 'Contact Name',
    phone VARCHAR(40) COMMENT 'Phone',
    wechat VARCHAR(80) COMMENT 'Wechat',
    pickup_date VARCHAR(40) COMMENT 'Pickup Date',
    remark VARCHAR(500) COMMENT 'Remark',
    status TINYINT DEFAULT 0 COMMENT '0-Pending 1-Approved 2-Rejected',
    review_note VARCHAR(255) COMMENT 'Review Note',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Donation Claim Records';

CREATE TABLE urgent_need (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    name VARCHAR(120) COMMENT 'Need Name',
    gap VARCHAR(80) COMMENT 'Gap Value',
    updated_at VARCHAR(32) COMMENT 'Updated At Display',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Urgent Donation Needs';

CREATE TABLE weekly_update (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    title VARCHAR(180) COMMENT 'Title',
    description VARCHAR(500) COMMENT 'Description',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Weekly Updates';

CREATE TABLE transparency_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    month VARCHAR(20) COMMENT 'Month Label',
    income VARCHAR(40) COMMENT 'Income Display',
    expense VARCHAR(40) COMMENT 'Expense Display',
    note VARCHAR(500) COMMENT 'Note',
    sort_order INT DEFAULT 0 COMMENT 'Sort Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Transparency Records';

CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    config_key VARCHAR(120) NOT NULL UNIQUE COMMENT 'Config Key',
    config_value VARCHAR(255) COMMENT 'Config Value',
    description VARCHAR(255) COMMENT 'Description',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System Configurations';

CREATE TABLE adoption_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key',
    animal_id BIGINT COMMENT 'Animal ID',
    user_id BIGINT COMMENT 'Applicant User ID',
    applicant_name VARCHAR(80) COMMENT 'Applicant Name',
    age INT COMMENT 'Age',
    job VARCHAR(120) COMMENT 'Job',
    income VARCHAR(40) COMMENT 'Income Level',
    address VARCHAR(255) COMMENT 'Address',
    phone VARCHAR(40) COMMENT 'Phone',
    wechat VARCHAR(80) COMMENT 'Wechat',
    housing VARCHAR(20) COMMENT 'Housing Type',
    experience VARCHAR(20) COMMENT 'Pet Experience',
    family_members VARCHAR(255) COMMENT 'Family Members',
    reason TEXT COMMENT 'Reason',
    status TINYINT DEFAULT 0 COMMENT '0-Pending 1-Approved 2-Rejected',
    review_note VARCHAR(255) COMMENT 'Review Note',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Adoption Applications';

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
INSERT INTO sys_user (username, nickname, password, role, avatar) VALUES 
('admin', '系统管理员', 'admin123', 'ADMIN', 'https://api.dicebear.com/9.x/avataaars/svg?seed=admin'),
('alice', 'Alice', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=alice'),
('bob', 'Bob', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=bob'),
('charlie', 'Charlie', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=charlie'),
('david', 'David', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=david'),
('eve', 'Eve', '123456', 'USER', 'https://api.dicebear.com/9.x/avataaars/svg?seed=eve');

INSERT INTO animal (animal_no, name, category, sex, body_size, age, avatar, is_sterilized, activity_scope, status, description, detail_content, location, latitude, longitude) VALUES
('SZ753', 'lucky', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958d0bef1a17.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '2025年11月救助的小黄白猫，性格活泼，适合家庭领养。', '<p>2025年11月救助的小黄白猫，状态稳定，等待领养。</p>', '深圳市福田区莲花山公园附近', 22.543096, 114.057865),
('SZ752', '小莎', 'CAT', 'FEMALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958cf541a948.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小莎为三花幼猫，健康活泼，适合室内饲养。', '<p>小莎来自深圳湾附近救助点，当前状态良好，可预约领养。</p>', '深圳湾公园观海栈道附近', 22.543096, 114.057865),
('SZ751', '小加', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958ce2d82c1e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小加是狸花幼猫，亲人，适应力较好。', '<p>小加由志愿者救助后进入领养中心，目前疫苗驱虫按计划进行。</p>', '深圳市南山区科苑地铁站周边', 22.543096, 114.057865),
('SZ750', '小桦', 'CAT', 'MALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/6958bf9831783.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '小桦为狸花幼猫，活泼好动，喜欢互动。', '<p>小桦与同批幼猫一起救助，健康状况稳定，等待新家庭。</p>', '深圳市宝安区西乡步行街附近', 22.543096, 114.057865),
('SZ749', '瑶瑶', 'CAT', 'FEMALE', 'SMALL', 0, 'http://adopt.it267.com/uploads/product/pic/68634a869c79e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '瑶瑶为橘猫幼猫，性格温顺。', '<p>瑶瑶于2025年救助入站，已完成基础体检，可申请领养。</p>', '深圳市罗湖区东湖公园南门附近', 22.543096, 114.057865),
('SZ719', '沙皮', 'DOG', 'MALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/667d1c46d11c1.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '沙皮为串串犬，亲人，适合家庭陪伴。', '<p>沙皮已完成基础免疫，具备领养条件。</p>', '深圳市龙华区清湖地铁站附近', 22.543096, 114.057865),
('SZ718', '黑豆', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/66485c0c007cf.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '黑豆为母串串，性格稳定。', '<p>黑豆目前状态良好，等待负责任领养家庭。</p>', '深圳市龙岗区大运中心附近', 22.543096, 114.057865),
('SZ717', '五百', 'DOG', 'MALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/6648590bc3772.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '五百为公串串，活泼外向。', '<p>五百亲人，适合有遛狗条件的家庭。</p>', '深圳市福田区香蜜湖公园周边', 22.543096, 114.057865),
('SZ711', '元宝', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/664704b0ea083.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '元宝为母串串，适合城市家庭。', '<p>元宝已在中心观察一段时间，状态稳定可领养。</p>', '深圳市福田区梅林公园附近', 22.543096, 114.057865),
('SZ710', '六斤', 'DOG', 'FEMALE', 'MEDIUM', 2, 'http://adopt.it267.com/uploads/product/pic/664704e1b359f.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80', 0, '深圳市', 1, '六斤体型适中，亲人。', '<p>六斤适应能力较好，建议有稳定作息的家庭领养。</p>', '深圳市南山区前海石公园附近', 22.543096, 114.057865);

-- Insert Sample Location History
INSERT INTO animal_location (animal_id, latitude, longitude) VALUES 
(1, 39.9042, 116.4074), (1, 39.9050, 116.4080), (1, 39.9060, 116.4090), (1, 39.9030, 116.4060);
INSERT INTO animal_location (animal_id, latitude, longitude) VALUES 
(2, 39.915, 116.404), (2, 39.916, 116.405), (2, 39.914, 116.403);

-- Insert Sample News
INSERT INTO news (title, summary, content, cover_image) VALUES 
('lucky 待领养档案', '猫 · 0岁 · 男孩，活动范围：深圳市', 'PET:SZ753', 'http://adopt.it267.com/uploads/product/pic/6958d0bef1a17.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80'),
('小莎 待领养档案', '猫 · 0岁 · 女孩，活动范围：深圳市', 'PET:SZ752', 'http://adopt.it267.com/uploads/product/pic/6958cf541a948.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80'),
('小加 待领养档案', '猫 · 0岁 · 男孩，活动范围：深圳市', 'PET:SZ751', 'http://adopt.it267.com/uploads/product/pic/6958ce2d82c1e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80'),
('小桦 待领养档案', '猫 · 0岁 · 男孩，活动范围：深圳市', 'PET:SZ750', 'http://adopt.it267.com/uploads/product/pic/6958bf9831783.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80'),
('瑶瑶 待领养档案', '猫 · 0岁 · 女孩，活动范围：深圳市', 'PET:SZ749', 'http://adopt.it267.com/uploads/product/pic/68634a869c79e.jpg?imageMogr2/thumbnail/!386x270r/gravity/Center/crop/386x270/quality/80');

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

INSERT INTO weekly_update (title, description, sort_order) VALUES
('本周新增救助 12 只', '其中幼犬 5 只、幼猫 7 只，均完成基础体检与隔离观察。', 1),
('本周成功领养 8 只', '已完成家访与回访流程，全部进入稳定适应期。', 2),
('本周完成绝育 6 只', '减少二次流浪与繁殖风险，持续推进科学救助。', 3);

INSERT INTO transparency_record (month, income, expense, note, sort_order) VALUES
('2026-01', '¥42,300', '¥39,180', '医疗与疫苗支出占比 49%', 1),
('2026-02', '¥38,600', '¥36,940', '粮食与寄养支出占比 44%', 2),
('2026-03', '¥45,200', '¥43,270', '新增救助数量上升，医疗支出增加', 3);

INSERT INTO urgent_need (name, gap, updated_at, sort_order) VALUES
('幼犬奶糕', '42 袋', '2026-04-05', 1),
('猫砂（膨润土）', '68 包', '2026-04-05', 2),
('体内外驱虫药', '26 盒', '2026-04-04', 3),
('保暖垫', '35 件', '2026-04-03', 4);

INSERT INTO donation_record (date, donor, item, quantity, unit, remark, sort_order) VALUES
('2026-05-28', '李明月', '皇家猫粮K36', '4', '袋', '幼猫专用', 1),
('2026-05-27', '深圳宠物爱心社', '豆腐猫砂', '20', '箱', '原味', 2),
('2026-05-26', '王建国', '犬用体内驱虫药', '50', '片', '中型犬剂量', 3),
('2026-05-25', '陈小雅', '宠物尿垫', '12', '包', 'L码加厚款', 4),
('2026-05-24', '张伟强', '顽皮鲜肉猫条', '30', '盒', '混合口味', 5),
('2026-05-23', '南山义工联', '宠物消毒液', '15', '瓶', '宠乐安品牌', 6),
('2026-05-22', '赵雨婷', '猫抓板', '10', '个', '大号瓦楞纸', 7),
('2026-05-21', '匿名爱心人士', '比瑞吉狗粮', '6', '袋', '15kg装', 8),
('2026-05-20', '刘思远', '宠物毛毯', '18', '条', '加绒保暖款', 9),
('2026-05-19', '喵汪之家志愿者', '猫罐头', '48', '罐', '希宝金罐', 10),
('2026-05-18', '周明辉', '皇家猫粮K36', '3', '袋', '成猫款', 11),
('2026-05-17', '黄丽华', '宠物玩具球', '25', '个', '橡胶发声球', 12),
('2026-05-16', '福田爱宠群', '犬用沐浴露', '8', '瓶', '低敏配方', 13),
('2026-05-15', '林俊杰', '幼犬奶粉', '12', '罐', '贝帮品牌', 14),
('2026-05-14', '郑晓雯', '豆腐猫砂', '16', '箱', '绿茶味', 15),
('2026-05-13', '深圳科技园义工', '宠物湿巾', '30', '包', '无酒精配方', 16),
('2026-05-12', '何志鹏', '犬用牵引绳', '14', '条', '中型犬适用', 17),
('2026-05-11', '匿名爱心人士', '猫粮试吃装', '60', '份', '多品牌混合', 18),
('2026-05-10', '孙婷婷', '宠物指甲剪', '10', '套', '带锉刀', 19),
('2026-05-09', '龙华流浪动物救助', '妙鲜包', '36', '袋', '鸡肉味', 20),
('2026-05-08', '马天宇', '宠物航空箱', '4', '个', '中号', 21),
('2026-05-07', '宝安宠物医院', '猫三联疫苗', '15', '支', '妙三多', 22),
('2026-05-06', '吴小燕', '宠物食盆', '20', '个', '不锈钢双盆', 23),
('2026-05-05', '匿名爱心人士', '犬用磨牙棒', '40', '包', '中大型犬', 24),
('2026-05-04', '罗湖区志愿者', '宠物尿垫', '10', '包', 'XL码', 25),
('2026-05-03', '许志强', '皇家猫粮K36', '5', '袋', '10kg装', 26),
('2026-05-02', '深圳大学义工社', '猫爬架', '3', '个', '多层实木', 27),
('2026-05-01', '蔡佳玲', '宠物益生菌', '24', '盒', '布拉迪酵母', 28),
('2026-04-30', '南山社区爱心群', '比瑞吉狗粮', '8', '袋', '小型犬款', 29),
('2026-04-29', '韩雨桐', '猫薄荷玩具', '22', '个', '含猫薄荷填充', 30);

INSERT INTO system_config (config_key, config_value, description) VALUES
('dashboard.totalRescueCount', '2680', '累计救助可编辑基准值'),
('dashboard.adoptionSuccessCount', '1930', '成功领养可编辑基准值');

INSERT INTO adoption_application (
    animal_id, user_id, applicant_name, age, job, income, address, phone, wechat, housing, experience, family_members, reason, status, review_note
) VALUES
(1, 2, '张三', 28, '产品经理', 'LEVEL3', '深圳市南山区xx路', '13800138000', 'zhangsan01', 'OWN', 'HAD', '夫妻二人', '希望为流浪猫提供长期稳定家庭。', 0, NULL),
(2, 3, '李四', 31, '设计师', 'LEVEL2', '深圳市福田区xx小区', '13900139000', 'lisi_pet', 'RENT', 'HAVE', '三口之家', '已有养宠经验，愿意接受回访。', 1, '资料完整，审核通过');
