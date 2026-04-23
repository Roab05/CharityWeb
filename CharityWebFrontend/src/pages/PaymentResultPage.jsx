import React from 'react';
import { useSearchParams, Link } from 'react-router-dom';

export default function PaymentResultPage() {
    const [searchParams] = useSearchParams();
    const responseCode = searchParams.get('vnp_ResponseCode');
    const transactionStatus = searchParams.get('vnp_TransactionStatus');
    const amount = searchParams.get('vnp_Amount');
    const txnRef = searchParams.get('vnp_TxnRef');

    const isSuccess = responseCode === '00' && (!transactionStatus || transactionStatus === '00');
    const formattedAmount = amount ? new Intl.NumberFormat('vi-VN').format(parseInt(amount) / 100) + ' ₫' : '';

    const getFailureMessage = () => {
        if (!responseCode) {
            return 'Giao dịch đã quá thời gian thanh toán hoặc không có phản hồi hợp lệ.';
        }

        const code = String(responseCode);
        const statusCode = transactionStatus ? String(transactionStatus) : null;

        if (code === '24' || statusCode === '02') {
            return 'Bạn đã hủy giao dịch thanh toán. Lịch sử giao dịch sẽ ghi nhận là thất bại.';
        }

        if (code === '91' || statusCode === '03') {
            return 'Giao dịch đã hết thời gian chờ (timeout). Lịch sử giao dịch sẽ ghi nhận là thất bại.';
        }

        return 'Giao dịch không thành công. Lịch sử giao dịch sẽ ghi nhận là thất bại.';
    };

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
                        <p className="text-gray-500 mb-4">{getFailureMessage()}</p>

                        {(responseCode || transactionStatus) && (
                            <div className="bg-red-50 rounded-xl p-4 mb-6 text-left text-sm text-red-700 space-y-1">
                                {txnRef && <p>Mã giao dịch: <span className="font-semibold">{txnRef}</span></p>}
                            </div>
                        )}
                    </>
                )}
                <div className="flex flex-col gap-3">
                    {!isSuccess && (
                        <Link to="/profile" className="btn-secondary text-center">Xem lịch sử giao dịch</Link>
                    )}
                    <Link to="/projects" className="btn-primary text-center">Quay lại dự án</Link>
                    <Link to="/" className="text-sm text-gray-500 hover:text-gray-700">Về trang chủ</Link>
                </div>
            </div>
        </div>
    );
}
