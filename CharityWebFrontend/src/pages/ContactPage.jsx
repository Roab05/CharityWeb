import React from "react";

/*
  ContactPage – Liên hệ với chúng tôi
*/
export default function ContactPage() {
  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-5xl mx-auto bg-white rounded-2xl shadow-sm p-8 grid md:grid-cols-2 gap-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-6">Liên hệ với chúng tôi</h1>

          <div className="space-y-4 text-gray-700">
            <p>
              <span className="font-semibold text-gray-800">📍 Địa chỉ:</span> 123 Đường ABC, Quận 1, TP.HCM
            </p>
            <p>
              <span className="font-semibold text-gray-800">📞 Hotline:</span> 1900-1234 (24/7)
            </p>
            <p>
              <span className="font-semibold text-gray-800">✉️ Email:</span>{" "}
              <a href="mailto:support@gayquy.vn" className="text-green-600 hover:underline">
                support@gayquy.vn
              </a>
            </p>
            <p>
              <span className="font-semibold text-gray-800">🕒 Giờ làm việc:</span> Thứ 2 - Chủ nhật: 8:00 - 22:00
            </p>
          </div>
        </div>

        <form className="space-y-4">
          <h2 className="text-xl font-semibold text-gray-800 mb-2">Gửi tin nhắn</h2>

          <div>
            <label className="block text-gray-700 mb-1">Họ và tên *</label>
            <input className="w-full border border-gray-300 rounded-md px-3 py-2" required />
          </div>

          <div>
            <label className="block text-gray-700 mb-1">Email *</label>
            <input type="email" className="w-full border border-gray-300 rounded-md px-3 py-2" required />
          </div>

          <div>
            <label className="block text-gray-700 mb-1">Chủ đề *</label>
            <input className="w-full border border-gray-300 rounded-md px-3 py-2" required />
          </div>

          <div>
            <label className="block text-gray-700 mb-1">Nội dung *</label>
            <textarea
              rows="4"
              className="w-full border border-gray-300 rounded-md px-3 py-2"
              required
            ></textarea>
          </div>

          <button
            type="submit"
            className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 transition"
          >
            Gửi tin nhắn
          </button>
        </form>
      </div>
    </div>
  );
}
