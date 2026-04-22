import React from 'react';

export default function LoadingSpinner({ size = 'md', text = '' }) {
    const sizes = {
        sm: 'h-6 w-6',
        md: 'h-10 w-10',
        lg: 'h-16 w-16',
    };

    return (
        <div className="flex flex-col items-center justify-center py-12">
            <div className={`${sizes[size]} animate-spin rounded-full border-4 border-gray-200 border-t-primary-600`} />
            {text && <p className="mt-4 text-gray-500 text-sm">{text}</p>}
        </div>
    );
}
