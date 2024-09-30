
'use client';

import React, {useEffect, useState} from 'react';
import {Feedback} from '@/app/lib/types/courses';
import {fetchFeedbacks} from "@/app/lib/api/courses/learningpath";
import FeedbackForm from "@/app/ui/learn/feedbackForn";
import FeedbackList from "@/app/ui/learn/feedbackList";

interface FeedbackClientWrapperProps {
    initialFeedbacks: Feedback[];
    learningPathId: string;
    professorId: string;
}

const FeedbackClientWrapper: React.FC<FeedbackClientWrapperProps> = ({
                                                                         initialFeedbacks,
                                                                         learningPathId,
                                                                         professorId
                                                                     }) => {
    const [feedbacks, setFeedbacks] = useState<Feedback[]>(initialFeedbacks);

    const handleFeedbackSaved = async () => {
        const updatedFeedbacks = await fetchFeedbacks(learningPathId);
        setFeedbacks(updatedFeedbacks);
    };

    return (
        <>
            <FeedbackForm
                learningPathId={learningPathId}
                professorId={professorId}
                onFeedbackSaved={handleFeedbackSaved}
            />
            <FeedbackList feedbacks={feedbacks} isStudentView={false}/>
        </>
    );
};

export default FeedbackClientWrapper;
