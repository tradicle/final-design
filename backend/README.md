# animal

包名：`com.xxx.animal`

## 数据库准备

1. 创建数据库 `animal_db` 并导入 `sql/init.sql`。
2. 修改 `src/main/resources/application.yml` 中的数据库配置（用户名/密码）。

## 运行

```bash
mvn spring-boot:run
```

## 测试接口

- GET `/api/animals/hello`
