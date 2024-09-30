'use client';

import React, { useState } from 'react';
import { Feedback } from '@/app/lib/types/courses';
import { format } from 'date-fns';

interface FeedbackListProps {
    feedbacks: Feedback[];
    isStudentView: boolean;
}

const FeedbackList: React.FC<FeedbackListProps> = ({ feedbacks, isStudentView }) => {
    const [currentPage, setCurrentPage] = useState(1);
    const feedbacksPerPage = 3;

    // Reverse the feedbacks to have the newest first
    const reversedFeedbacks = [...feedbacks].reverse();

    const lastFeedbackIndex = currentPage * feedbacksPerPage;
    const firstFeedbackIndex = lastFeedbackIndex - feedbacksPerPage;
    const currentFeedbacks = reversedFeedbacks.slice(firstFeedbackIndex, lastFeedbackIndex);

    const filteredFeedbacks = isStudentView
        ? currentFeedbacks.filter(f => f.feedbackType === 'DIRECT')
        : currentFeedbacks;

    const totalPages = Math.ceil(feedbacks.length / feedbacksPerPage);

    return (
        <div className={"mt-2"}>
            {filteredFeedbacks.map((feedback) => (
                <div key={feedback.id} className="border-b py-2">
                    <p>{feedback.content}</p>
                    <span className="text-sm text-gray-500">
                        {format(new Date(feedback.createdAt), 'dd/MM/yyyy, HH:mm:ss')}
                    </span>
                    {!isStudentView && (
                        <span className="ml-2 text-xs text-blue-500">
                            {feedback.feedbackType === 'DIRECT' ? 'Direct' : 'LLM'}
                        </span>
                    )}
                </div>
            ))}
            {totalPages > 1 && (
                <div className="mt-4 flex justify-between">
                    <button
                        onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                        disabled={currentPage === 1}
                        className="text-blue-600 hover:underline disabled:text-gray-400"
                    >
                        Previous
                    </button>
                    <button
                        onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                        disabled={currentPage === totalPages}
                        className="text-blue-600 hover:underline disabled:text-gray-400"
                    >
                        Next
                    </button>
                </div>
            )}
        </div>
    );
};

export default FeedbackList;
