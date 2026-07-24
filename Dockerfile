# ビルドステージ：Mavenで依存関係を取得してjarをビルドする
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# 実行ステージ：ビルド済みjarだけを積んだ軽量イメージ
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# ★Renderの無料プランはメモリ上限512MBしかなく、JVMのデフォルト設定のままだと
# 起動後にヒープが膨らんでOOM Kill（exit code 137）でクラッシュを繰り返していた。
# ヒープとメタスペースの上限を明示的に絞り、GCも小メモリ環境向けのSerialGCに変更する。
ENTRYPOINT ["java", "-Xmx350m", "-XX:MaxMetaspaceSize=100m", "-XX:+UseSerialGC", "-jar", "app.jar"]
