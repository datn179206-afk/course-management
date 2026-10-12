# Mini Project: Quản lý khóa học (Java Spring Boot)

Spring Boot 3.3 + Spring Data JPA + Thymeleaf + SQL Server (instance `SQLEXPRESS`, database `CourseDb`).

## Cách chạy (làm lần lượt)

**Bước 1. Tạo database và mật khẩu cho `sa` (1 lần)**
Mở SSMS → File → Open → File → chọn `database/create-database.sql` → bấm Execute (F5).
File này tạo database `CourseDb` và đặt mật khẩu `sa` là `Abc@12345`.

**Bước 2. Cho phép đăng nhập bằng user/mật khẩu (1 lần)**
Trong SSMS: chuột phải vào server `localhost\SQLEXPRESS` → Properties → Security →
chọn **SQL Server and Windows Authentication mode** → OK → chuột phải server → Restart.

**Bước 3. Bật SQL Server Browser (1 lần)**
Windows → gõ `Services` → tìm **SQL Server Browser** → Properties → Startup type = Automatic → Start.

**Bước 4. Chạy ứng dụng**
Mở thư mục project bằng VS Code, mở `CourseApplication.java` và bấm Run
(hoặc gõ `mvn spring-boot:run` trong terminal). Lần đầu Maven sẽ tải thư viện nên hơi lâu.
Khi terminal có dòng `Started CourseApplication` là xong.

**Bước 5. Mở trình duyệt**: http://localhost:8080

Ứng dụng tự tạo 3 bảng và nạp dữ liệu mẫu. Xem trong SSMS: chuột phải **Databases → Refresh**
→ `CourseDb → Tables` (`courses`, `course_categories`, `course_images`).

Nếu đặt mật khẩu `sa` khác `Abc@12345`, sửa `spring.datasource.password` trong
`src/main/resources/application.properties`.

### Lỗi thường gặp

| Lỗi trong log | Cách xử lý |
|---|---|
| `Login failed for user 'sa'` | Làm lại Bước 1 và Bước 2 (nhớ Restart server) |
| `instance name ... could not be found` / `Connection refused` | Làm lại Bước 3 |
| `Cannot open database "CourseDb"` | Làm lại Bước 1 |
| Tiếng Việt thành `?` | Giữ dòng `use_nationalized_character_data=true`; xóa DB `CourseDb`, tạo lại, chạy lại |

## Đối chiếu yêu cầu đề bài

| Yêu cầu | Nằm ở đâu |
|---|---|
| 3 model, quan hệ 1-n | `model/CourseCategory`, `Course`, `CourseImage` (`@OneToMany` / `@ManyToOne`) |
| Dữ liệu mẫu: 3 loại, 7 khóa học, có ảnh | `config/DataSeeder` |
| Validation (tên, học phí > 0, thời lượng > 0, loại tồn tại, tên không trùng trong loại) | `dto/CourseForm` + `CourseController.checkBusinessRules` + unique constraint ở `Course` |
| Create/Edit chọn loại từ database | `templates/courses/form.html` |
| Tìm kiếm + lọc loại + lọc trạng thái + khoảng học phí + sắp xếp (đồng thời) | `repository/CourseSpecifications` (một câu SQL, không lặp lọc trong Java) |
| Giữ lại điều kiện đã nhập trên form | `th:object="${filter}"` + `th:field` trong `courses/list.html` |
| Ngắn hạn (<= 30) / Dài hạn (> 30), không có cột trong DB | `Course.getDurationType()` (`@Transient`) |
| Thêm ảnh bằng URL, hiển thị ở Detail, chọn/đổi ảnh đại diện (tối đa 1) | `CourseService.addImage / setThumbnail`, `courses/detail.html` |
| Ràng buộc 1: không xóa loại còn khóa học | `CategoryService.delete` |
| Ràng buộc 2: `IsActive = false` vẫn hiện, ghi "Ngừng tuyển sinh" | `Course.getStatusText()`, `courses/list.html` |
| Ràng buộc 3: lọc "Đang tuyển sinh" chỉ lấy `IsActive = true` | `CourseSpecifications` |
| Xử lý lỗi (id không tồn tại, min > max, chưa có ảnh, không có kết quả, form sai) | `GlobalExceptionHandler`, `CourseController`, các template |

## Kịch bản demo (14 mục trong đề)

1. Mở SSMS → `CourseDb` → xem 3 bảng và khóa ngoại (Database Diagrams hoặc `SELECT`).
2. `/courses` → danh sách có cột "Loại khóa học".
3. Gõ `react` vào ô tên → tìm kiếm; nhập tên không có thật → thông báo không có kết quả.
4. Chọn loại khóa học / trạng thái "Đang tuyển sinh" / học phí 1.000.000 – 4.500.000 / sắp xếp → bấm Tìm kiếm (các điều kiện vẫn giữ nguyên trên form).
5. Nhập học phí tối thiểu 5.000.000, tối đa 1.000.000 → báo lỗi, không crash.
6. Cột "Phân loại": Ngắn hạn / Dài hạn. Khóa "Android Kotlin" (30 giờ) là Ngắn hạn.
7. "Thêm khóa học" / "Sửa" → thử để trống tên, học phí 0, trùng tên trong cùng loại.
8. Vào Chi tiết → thêm ảnh bằng URL → bấm "Đặt làm ảnh đại diện" (ảnh cũ tự thành ảnh thường).
9. `/categories` → xóa "Lập trình Web" → bị chặn vì còn khóa học.
10. Truy cập `/courses/9999` → trang thông báo "Không tìm thấy dữ liệu".

## Cấu trúc thư mục

```
database/create-database.sql
src/main/java/com/example/course
├── config/DataSeeder.java
├── controller/        CourseController, CategoryController
├── dto/               CourseForm, CourseFilter
├── exception/         ResourceNotFoundException, GlobalExceptionHandler
├── model/             CourseCategory, Course, CourseImage
├── repository/        CourseRepository, CourseCategoryRepository, CourseSpecifications
└── service/           CourseService, CategoryService
src/main/resources
├── templates/         courses/, categories/, fragments.html, error-page.html
└── static/css/style.css
```
