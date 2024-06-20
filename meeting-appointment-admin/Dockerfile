# 使用 openjdk:21 作为基础镜像
FROM openjdk:21

# 将外部JAR文件添加到容器的/app.jar路径
# <JAR_PATH> 需要被构建或提供的具体JAR文件路径代替
ADD <JAR_PATH> /app.jar
ADD meeting-appointment-admin/src/main/resources/apply/会议纪要.xlsx /app/apply
ADD meeting-appointment-admin/src/main/resources/apply/保险问答系统Sprint02回顾会议纪要.docx /app/apply
ADD meeting-appointment-admin/src/main/resources/apply/image.png /app/apply
ADD meeting-appointment-admin/src/main/resources/backups/会议纪要.xlsx /app/backups