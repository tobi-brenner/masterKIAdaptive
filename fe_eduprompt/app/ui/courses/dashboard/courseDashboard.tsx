'use client';
import {Course, ExtendedLearningStatistics, LearningStatistics, UserLearningPath} from "@/app/lib/types/courses";
import AggregatedBurndownChart from "@/app/ui/courses/dashboard/aggregatedBurndownChart";
import {User} from "@/app/lib/types/users";
import {useEffect, useState} from "react";
import {fetchFromAPI} from "@/app/lib/utils/api";




interface CourseDashboardProps {
    course: Course;
    userLearningPaths: UserLearningPath[];
}

export default function CourseDashboard({ course, userLearningPaths }: CourseDashboardProps) {
    const [learningStatistics, setLearningStatistics] = useState<ExtendedLearningStatistics[]>([]);

    useEffect(() => {
        const fetchAllLearningStatistics = async () => {
            try {
                const allStatistics = await Promise.all(
                    userLearningPaths.map(async ({ id: userId, learningPathId, email, firstName,lastName }) => {
                        if (!learningPathId) return [];
                        const response = await fetchFromAPI<LearningStatistics[]>(`/learning-statistics/${userId}/learning-path/${learningPathId}/course/${course.id}`);
                        // return response;
                        return response.map(stat => ({
                            ...stat,
                            user: {
                                id: userId,
                                firstName,
                                lastName,
                                email,
                            },
                        }));
                    })
                );

                const flattenedStatistics = allStatistics.flat();
                setLearningStatistics(flattenedStatistics);
            } catch (error) {
                console.error("Error fetching learning statistics:", error);
            }
        };

        fetchAllLearningStatistics();
    }, [userLearningPaths, course.id]);

    return (
        <div className={"w-3/4"}>
            <AggregatedBurndownChart course={course} learningStatistics={learningStatistics} users={userLearningPaths} />
        </div>
    );
}