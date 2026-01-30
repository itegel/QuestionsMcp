# 任务
1. 初始化一个mcp服务
2. 实现根据question_id获取，题目信息（题干、题目内容）和跟该question_id关联的所有学生提问内容

# 上下文
1. 我需要实现一个mcp服务，为我的另一个agent提供根据question_id获取题目信息和学生提问内容的功能
2. 表结构参考 @sql.md

# 限制
1. 输入学校id、question_id
2. 输出题目信息（题干、题目内容）和跟该question_id关联的所有学生提问内容，提问内容在question_ding.comment字段

# 技术栈
1. 数据库：mysql
2. 编程语言：java 1.8
3. 框架：spring boot
