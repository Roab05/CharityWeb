import React from 'react';
import { useSearchParams, Link } from 'react-router-dom';

export default function PaymentResultPage() {
    const [searchParams] = useSearchParams();
    const responseCode = searchParams.get('vnp_ResponseCode');
    const amount = searchParams.get('vnp_Amount');
    const txnRef = searchParams.get('vnp_TxnRef');

    const isSuccess = responseCode === '00';
    const formattedAmount = amount ? new Intl.NumberFormat('vi-VN').format(parseInt(amount) / 100) + ' ₫' : '';

    return (
        <div className="min-h-[60vh] flex items-center justify-center px-4">
            <div className="card p-10 max-w-md w-full text-center">
                {isSuccess ? (
                    <>
                        <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-10 h-10 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                            </svg>
                        </div>
                        <h1 className="text-2xl font-bold text-gray-800 mb-2">Thanh toán thành công!</h1>
                        <p className="text-gray-500 mb-6">Cảm ơn bạn đã ủng hộ. Đóng góp của bạn đã được ghi nhận.</p>
                        {formattedAmount && (
                            <div className="bg-green-50 rounded-xl p-4 mb-6">
                                <p className="text-sm text-gray-500">Số tiền</p>
                                <p className="text-2xl font-bold text-green-600">{formattedAmount}</p>
                            </div>
                        )}
                    </>
                ) : (
                    <>
                        <div className="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-10 h-10 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </div>
                        <h1 className="text-2xl font-bold text-gray-800 mb-2">Thanh toán thất bại</h1>
                        <p className="text-gray-500 mb-6">
                            {responseCode ? 'Giao dịch không thành công. Vui lòng thử lại.' : 'Không có thông tin thanh toán.'}
                        </p>
                    </>
                )}
                <div className="flex flex-col gap-3">
                    <Link to="/projects" className="btn-primary text-center">Quay lại dự án</Link>
                    <Link to="/" className="text-sm text-gray-500 hover:text-gray-700">Về trang chủ</Link>
                </div>
            </div>
        </div>
    );
}
