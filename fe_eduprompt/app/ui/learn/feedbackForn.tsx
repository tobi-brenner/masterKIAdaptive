
'use client';

import React, { useState } from 'react';
import {FeedbackData} from "@/app/lib/types/courses";
import {createFeedback} from "@/app/lib/api/courses/learningpath";

interface FeedbackFormProps {
    learningPathId: string;
    professorId: string;
    onFeedbackSaved: () => void;
}

const FeedbackForm: React.FC<FeedbackFormProps> = ({ learningPathId, professorId, onFeedbackSaved }) => {
    const [content, setContent] = useState('');
    const [feedbackType, setFeedbackType] = useState<'DIRECT' | 'LLM'>('DIRECT'); // Default to DIRECT
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setLoading(true);
        setError(null);

        let isIngestedToLLM=false;
        const feedbackData: FeedbackData = {
            content,
            learningPathId,
            professorId,
            feedbackType,
            isIngestedToLLM
        };

        try {
            await createFeedback(feedbackData);
            onFeedbackSaved(); // Refresh the feedback list
            setContent(''); // Reset form
        } catch (err) {
            setError('Failed to submit feedback.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="Write your feedback..."
                className="border p-2 w-full"
                required
            />
            <div className="mt-2">
                <label>
                    <input
                        type="radio"
                        name="feedbackType"
                        value="DIRECT"
                        checked={feedbackType === 'DIRECT'}
                        onChange={() => setFeedbackType('DIRECT')}
                    />
                    Direct Feedback
                </label>
                <label className="ml-4">
                    <input
                        type="radio"
                        name="feedbackType"
                        value="LLM"
                        checked={feedbackType === 'LLM'}
                        onChange={() => setFeedbackType('LLM')}
                    />
                    LLM Feedback
                </label>
            </div>
            <button type="submit" disabled={loading} className="mt-4 bg-blue-500 text-white py-2 px-4 rounded">
                {loading ? 'Submitting...' : 'Submit Feedback'}
            </button>
            {error && <p className="text-red-500 mt-2">{error}</p>}
        </form>
    );
};

export default FeedbackForm;


