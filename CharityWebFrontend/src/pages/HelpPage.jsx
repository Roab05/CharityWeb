import React, { useState } from "react";

/*
  HelpPage – Trung tâm trợ giúp (FAQ + Tabs)
*/
export default function HelpPage() {
  const [activeTab, setActiveTab] = useState("faq");

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto bg-white rounded-2xl shadow-sm p-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Trung tâm trợ giúp</h1>

        <div className="flex space-x-2 mb-8">
          {[
            { key: "faq", label: "Câu hỏi thường gặp" },
            { key: "guide", label: "Hướng dẫn sử dụng" },
            { key: "support", label: "Hỗ trợ trực tiếp" },
          ].map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-4 py-2 rounded-md font-medium ${
                activeTab === tab.key
                  ? "bg-green-600 text-white"
                  : "bg-gray-100 text-gray-700"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {activeTab === "faq" && (
          <div className="space-y-4">
            {[
              {
                q: "Làm thế nào để tạo dự án gây quỹ?",
                a: "Bạn cần đăng ký tài khoản, sau đó điền đầy đủ thông tin dự án và chờ phê duyệt từ đội ngũ của chúng tôi.",
              },
              {
                q: "Tôi có thể ủng hộ bằng những hình thức nào?",
                a: "Chúng tôi hỗ trợ thanh toán qua thẻ ngân hàng, ví điện tử và chuyển khoản ngân hàng.",
              },
              {
                q: "Làm sao để theo dõi tiến độ dự án?",
                a: "Bạn có thể theo dõi tiến độ trực tiếp trên trang dự án hoặc đăng ký nhận thông báo qua email.",
              },
              {
                q: "Có thể hủy đóng góp không?",
                a: "Đóng góp chỉ có thể hủy trong trường hợp dự án bị hủy bỏ hoặc không đạt mục tiêu tối thiểu.",
              },
            ].map((item, i) => (
              <div
                key={i}
                className="border border-gray-200 rounded-xl p-4 hover:shadow-sm transition"
              >
                <h3 className="font-semibold text-gray-800 mb-1">{item.q}</h3>
                <p className="text-gray-600">{item.a}</p>
              </div>
            ))}
          </div>
        )}

        {activeTab === "guide" && (
          <p className="text-gray-600">
            Hướng dẫn chi tiết sẽ được cập nhật trong thời gian tới.
          </p>
        )}
        {activeTab === "support" && (
          <p className="text-gray-600">
            Vui lòng liên hệ qua email <b>support@gayquy.vn</b> để được hỗ trợ trực tiếp.
          </p>
        )}
      </div>
    </div>
  );
}
