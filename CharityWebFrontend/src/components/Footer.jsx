import React from 'react';
/*
 Footer component
 Props:
 - setCurrentPage, setSelectedProject: navigation helpers
*/
export default function Footer({ setCurrentPage, setSelectedProject }) {
  const scrollToTop = () => { window.scrollTo({ top: 0, behavior: 'smooth' }); };
  const goto = (page) => { setSelectedProject(null); setCurrentPage(page); scrollToTop(); };
  return (
    <footer className="bg-gray-800 text-white py-12">
      <div className="container mx-auto px-4">
        <div className="grid md:grid-cols-2 gap-8">
          <div>
            <h3 className="text-xl font-bold mb-4">🌱 GayQuy.vn</h3>
            <p className="text-gray-300">Nền tảng gây quỹ cộng đồng hàng đầu Việt Nam</p>
          </div>
          <div>
            <h4 className="font-semibold mb-4">Liên kết nhanh</h4>
            <div className="grid grid-cols-2 gap-4">
              <ul className="space-y-2 text-gray-300">
                <li><button onClick={() => goto('about')} className="hover:text-white transition-colors text-left">Về chúng tôi</button></li>
                <li><button onClick={() => goto('terms')} className="hover:text-white transition-colors text-left">Điều khoản</button></li>
              </ul>
              <ul className="space-y-2 text-gray-300">
                <li><button onClick={() => goto('help')} className="hover:text-white transition-colors text-left">Trung tâm trợ giúp</button></li>
                <li><button onClick={() => goto('contact')} className="hover:text-white transition-colors text-left">Liên hệ</button></li>
              </ul>
            </div>
          </div>
        </div>
        <div className="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
          <p>&copy; 2025 GayQuy.vn. Tất cả quyền được bảo lưu.</p>
        </div>
      </div>
    </footer>
  );
}
