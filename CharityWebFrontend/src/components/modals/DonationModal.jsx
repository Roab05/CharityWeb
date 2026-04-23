import React, { useState } from 'react';
import { initiateDonation } from '../../services/DonationService';

export default function DonationModal({ show, onClose, project, user }) {
    const [amount, setAmount] = useState('');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const presetAmounts = [50000, 100000, 200000, 500000, 1000000, 2000000];

    const formatCurrency = (val) => new Intl.NumberFormat('vi-VN').format(val);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!amount || parseInt(amount) < 10000) {
            setError('Số tiền tối thiểu là 10.000 ₫');
            return;
        }
        setLoading(true);
        try {
            const res = await initiateDonation(project.projectId, {
                amount: parseInt(amount),
                message: message.trim() || null,
            });
            if (res.data?.paymentUrl) {
                window.location.href = res.data.paymentUrl;
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        onClose();
        setAmount('');
        setMessage('');
        setError('');
    };

    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={handleClose}>
            <div className="bg-white rounded-2xl p-8 max-w-lg w-full shadow-2xl" onClick={(e) => e.stopPropagation()}>
                <div className="flex justify-between items-center mb-6">
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800">Ủng hộ dự án</h2>
                        <p className="text-sm text-gray-500 mt-1 line-clamp-1">{project?.projectName}</p>
                    </div>
                    <button className="text-gray-400 hover:text-gray-600 p-1" onClick={handleClose}>
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {!user ? (
                    <div className="text-center py-8">
                        <div className="text-5xl mb-4">🔒</div>
                        <p className="text-gray-600 mb-4">Vui lòng đăng nhập để ủng hộ dự án này</p>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit} className="space-y-5">
                        {error && (
                            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
                        )}

                        {/* Preset amounts */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">Chọn nhanh</label>
                            <div className="grid grid-cols-3 gap-2">
                                {presetAmounts.map((preset) => (
                                    <button
                                        key={preset}
                                        type="button"
                                        onClick={() => setAmount(preset.toString())}
                                        className={`py-2 px-3 rounded-lg text-sm font-medium border transition-all ${parseInt(amount) === preset
                                                ? 'border-primary-500 bg-primary-50 text-primary-700'
                                                : 'border-gray-200 hover:border-primary-300 text-gray-600'
                                            }`}
                                    >
                                        {formatCurrency(preset)} ₫
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* Custom amount */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1.5">
                                Số tiền ủng hộ (₫) <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="number"
                                min="10000"
                                className="input-field text-lg font-semibold"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                placeholder="Nhập số tiền"
                                required
                            />
                        </div>

                        {/* Message */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1.5">Lời nhắn (không bắt buộc)</label>
                            <textarea
                                className="input-field resize-none"
                                rows="3"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                placeholder="Gửi lời động viên đến dự án..."
                                maxLength={500}
                            />
                        </div>

                        <div className="bg-blue-50 rounded-lg p-3 text-sm text-blue-700">
                            💳 Bạn sẽ được chuyển đến cổng thanh toán VNPay để hoàn tất giao dịch
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className={`w-full py-3 rounded-lg font-semibold text-white transition-all ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary-600 hover:bg-primary-700 shadow-sm hover:shadow-md'
                                }`}
                        >
                            {loading ? 'Đang xử lý...' : `Ủng hộ ${amount ? formatCurrency(parseInt(amount)) + ' ₫' : ''}`}
                        </button>
                    </form>
                )}
            </div>
        </div>
    );
}
