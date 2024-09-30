'use client';
import {useState, useEffect} from "react";
import CourseMaterials from "@/app/ui/learn/courseMaterials";
import LearningProgress from "@/app/ui/learn/learningProgress";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {Course, Feedback, LearningStatistics} from "@/app/lib/types/courses";
import CourseInformation from "@/app/ui/learn/courseInformation";
import FeedbackList from "@/app/ui/learn/feedbackList";
import {fetchFeedbacks} from "@/app/lib/api/courses/learningpath";

interface TabGroupProps {
    userId: string;
    learningPathId: string;
    course: Course;
    feedbacks: Feedback[]
}

const TabGroup: React.FC<TabGroupProps> = ({userId, learningPathId, course, feedbacks}) => {
    const [activeTab, setActiveTab] = useState<string | null>(null);
    const [learningStatistics, setLearningStatistics] = useState<LearningStatistics[]>([]);
    const [courseMaterials, setCourseMaterials] = useState(course.courseMaterial);

    // Fetch learning statistics once
    useEffect(() => {
        const fetchLearningStatistics = async () => {
            try {
                const response: LearningStatistics[] = await fetchFromAPI<LearningStatistics[]>(`/learning-statistics/${userId}/learning-path/${learningPathId}/course/${course.id}`)
                ;
                setLearningStatistics(response);
            } catch (error) {
                console.error("Error fetching learning statistics:", error);
            }
        };

        fetchLearningStatistics();
    }, [userId, learningPathId, course.id]);

    return (
        <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
            {/* Tab Buttons */}
            <div className="flex space-x-4 mb-4">
                <button
                    className={`px-4 py-2 font-medium border-b-2 ${activeTab === 'info' ? 'border-blue-500 text-blue-500' : 'border-gray-300 text-gray-600'}`}
                    onClick={() => setActiveTab(activeTab === 'info' ? null : 'info')}
                >
                    Kursinformationen
                </button>
                <button
                    className={`px-4 py-2 font-medium border-b-2 ${activeTab === 'materials' ? 'border-blue-500 text-blue-500' : 'border-gray-300 text-gray-600'}`}
                    onClick={() => setActiveTab(activeTab === 'materials' ? null : 'materials')}
                >
                    Kursmaterial
                </button>
                <button
                    className={`px-4 py-2 font-medium border-b-2 ${activeTab === 'progress' ? 'border-blue-500 text-blue-500' : 'border-gray-300 text-gray-600'}`}
                    onClick={() => setActiveTab(activeTab === 'progress' ? null : 'progress')}
                >
                    Lernfortschritt
                </button>
                <button
                    className={`px-4 py-2 font-medium border-b-2 ${activeTab === 'feedback' ? 'border-blue-500 text-blue-500' : 'border-gray-300 text-gray-600'}`}
                    onClick={() => setActiveTab(activeTab === 'feedback' ? null : 'feedback')}
                >
                    Feedback
                </button>
            </div>

            {/* Conditionally Render Content Based on Active Tab */}
            {activeTab === 'info' && (
                <CourseInformation course={course}/>
            )}
            {activeTab === 'materials' && (
                <CourseMaterials courseMaterials={courseMaterials}/>
            )}
            {activeTab === 'progress' && (
                <LearningProgress userId={userId} learningPathId={learningPathId} course={course}
                                  learningStatistics={learningStatistics}/>
            )}
            {activeTab === 'feedback' && (
                <FeedbackList feedbacks={feedbacks} isStudentView={true} />
            )}
        </div>
    );
};

export default TabGroup;