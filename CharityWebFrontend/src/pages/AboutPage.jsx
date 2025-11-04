/*
  AboutPage – Trang giới thiệu
  Hiển thị thông tin về GayQuy.vn, sứ mệnh, giá trị cốt lõi và thành tựu.
*/
export default function AboutPage() {
  return (
    <div className="bg-white py-12">
      <div className="max-w-5xl mx-auto px-6">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Về GayQuy.vn</h1>
        <p className="text-gray-700 mb-8">
          <strong>GayQuy.vn</strong> là nền tảng gây quỹ cộng đồng hàng đầu tại Việt Nam, được
          thành lập với sứ mệnh kết nối những tấm lòng hảo tâm với các dự án ý nghĩa trên khắp cả nước.
        </p>

        <h2 className="text-2xl font-semibold text-green-700 mb-2">Sứ mệnh</h2>
        <p className="text-gray-700 mb-8">
          Chúng tôi tin rằng mỗi đóng góp nhỏ đều có thể tạo nên sự thay đổi lớn. Thông qua nền tảng
          của mình, chúng tôi mong muốn tạo ra một cộng đồng đoàn kết, nơi mọi người có thể dễ dàng
          tham gia vào các hoạt động từ thiện và tạo ra tác động tích cực cho xã hội.
        </p>

        <h2 className="text-2xl font-semibold text-green-700 mb-4">Giá trị cốt lõi</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-10">
          <div className="bg-green-50 p-5 rounded-xl text-center shadow-sm">
            <div className="text-4xl mb-2">🎯</div>
            <h3 className="font-bold text-gray-800">Minh bạch</h3>
            <p className="text-gray-600 text-sm">Mọi khoản đóng góp đều được theo dõi và báo cáo chi tiết.</p>
          </div>
          <div className="bg-green-50 p-5 rounded-xl text-center shadow-sm">
            <div className="text-4xl mb-2">🤝</div>
            <h3 className="font-bold text-gray-800">Cộng đồng</h3>
            <p className="text-gray-600 text-sm">Kết nối hàng triệu người Việt cùng chung tay làm từ thiện.</p>
          </div>
          <div className="bg-green-50 p-5 rounded-xl text-center shadow-sm">
            <div className="text-4xl mb-2">💚</div>
            <h3 className="font-bold text-gray-800">Ý nghĩa</h3>
            <p className="text-gray-600 text-sm">Tạo ra những tác động tích cực và bền vững cho xã hội.</p>
          </div>
        </div>

        <h2 className="text-2xl font-semibold text-green-700 mb-4">Thành tựu</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 text-center">
          <div className="bg-green-50 rounded-xl p-6 shadow-sm">
            <p className="text-3xl font-bold text-green-700 mb-1">250+</p>
            <p className="text-gray-700">Dự án đã được hỗ trợ</p>
          </div>
          <div className="bg-green-50 rounded-xl p-6 shadow-sm">
            <p className="text-3xl font-bold text-green-700 mb-1">2,000+</p>
            <p className="text-gray-700">Người dùng tích cực</p>
          </div>
        </div>
      </div>
    </div>
  );
}
