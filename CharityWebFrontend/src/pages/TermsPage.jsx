
/*
  TermsPage – Trang điều khoản sử dụng
*/
export default function TermsPage() {
  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-6 bg-white rounded-2xl shadow-sm p-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Điều khoản sử dụng</h1>

        <section className="mb-6">
          <h2 className="text-green-700 font-semibold text-xl mb-2">1. Giới thiệu</h2>
          <p className="text-gray-700">
            Chào mừng bạn đến với <strong>GayQuy.vn</strong>. Bằng việc sử dụng nền tảng của chúng tôi,
            bạn đồng ý tuân thủ các điều khoản và điều kiện được nêu dưới đây.
          </p>
        </section>

        <section className="mb-6">
          <h2 className="text-green-700 font-semibold text-xl mb-2">2. Quyền và nghĩa vụ của người dùng</h2>
          <h3 className="font-semibold text-gray-800 mt-3 mb-1">Quyền của người dùng:</h3>
          <ul className="list-disc list-inside text-gray-700 space-y-1">
            <li>Tạo và quản lý dự án gây quỹ</li>
            <li>Ủng hộ các dự án ý nghĩa</li>
            <li>Theo dõi tiến độ các dự án</li>
            <li>Nhận thông tin cập nhật về dự án</li>
          </ul>

          <h3 className="font-semibold text-gray-800 mt-4 mb-1">Nghĩa vụ của người dùng:</h3>
          <ul className="list-disc list-inside text-gray-700 space-y-1">
            <li>Cung cấp thông tin chính xác và trung thực</li>
            <li>Không sử dụng nền tảng cho mục đích bất hợp pháp</li>
            <li>Tôn trọng quyền riêng tư của người khác</li>
            <li>Tuân thủ quy định của pháp luật Việt Nam</li>
          </ul>
        </section>

        <section>
          <h2 className="text-green-700 font-semibold text-xl mb-2">3. Quy định về dự án</h2>
          <p className="text-gray-700 mb-2">
            Tất cả các dự án trên nền tảng phải:
          </p>
          <ul className="list-disc list-inside text-gray-700 space-y-1">
            <li>Có mục đích thiện nguyện, xã hội hoặc cộng đồng</li>
            <li>Công khai tiến độ và kết quả gây quỹ</li>
            <li>Cam kết minh bạch về tài chính</li>
          </ul>
        </section>
      </div>
    </div>
  );
}
