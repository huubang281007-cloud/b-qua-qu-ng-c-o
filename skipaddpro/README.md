# Skip Ad Pro 2.0

Tự động bấm nút "Bỏ qua quảng cáo" trên mọi bản YouTube (YouTube, Music, Go, Kids, TV, Vanced, ReVanced) và trình duyệt.

## Cách build ra APK

**Cách 1 – Android Studio (dễ nhất)**: Mở thư mục này → chờ Gradle Sync → Build > Build APK(s).
APK nằm ở `app/build/outputs/apk/debug/app-debug.apk`.

**Cách 2 – GitHub Actions (không cần cài gì, làm được trên điện thoại)**:
Tạo repo GitHub, upload toàn bộ thư mục này (giữ nguyên `.github/workflows/build.yml`),
vào tab Actions → "Build APK" → Run workflow → tải artifact `SkipAdPro-debug-apk`.

**Cách 3 – Dòng lệnh**: cài Gradle 8.7 + JDK 17 + Android SDK, rồi chạy `gradle assembleDebug`.

## Lưu ý
- Giữ `applicationId = com.example.skipad` nên có thể cài đè bản cũ nếu cùng khoá ký debug; nếu báo xung đột thì gỡ bản cũ rồi cài lại.
- Android 13+: bật "Cho phép cài đặt bị hạn chế" trong Thông tin ứng dụng trước khi bật Trợ năng.
- Chỉ bỏ qua được quảng cáo có nút "Bỏ qua".
