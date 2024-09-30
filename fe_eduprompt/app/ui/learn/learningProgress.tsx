'use client';
import { BloomLevel, Course, LearningStatistics, Topic } from "@/app/lib/types/courses";
import BurndownChartByStep from "@/app/ui/learn/burndownChartByStep";

interface ProgressProps {
    userId: string;
    learningPathId: string;
    course: Course;
    learningStatistics: LearningStatistics[];
}

const LearningProgress: React.FC<ProgressProps> = ({ userId, learningPathId, course, learningStatistics }) => {
    // Helper to map BloomLevel to number
    const bloomLevelOrder: { [key in BloomLevel]: number } = {
        [BloomLevel.NONE]: 0,
        [BloomLevel.REMEMBERING]: 1,
        [BloomLevel.UNDERSTANDING]: 2,
        [BloomLevel.APPLYING]: 3,
        [BloomLevel.ANALYZING]: 4,
        [BloomLevel.EVALUATING]: 5,
        [BloomLevel.CREATING]: 6,
    };

    // Get the progress of a topic based on Bloom level
    const getMaxBloomLevelProgress = (topic: Topic): number => {
        const statisticsForTopic = learningStatistics.filter(stat => stat.topicId === topic.id);

        if (statisticsForTopic.length === 0) {
            return 0;
        }

        const maxBloom = bloomLevelOrder[topic.maxBloom]; // Convert topic.maxBloom to number
        const highestBloomAchieved = Math.min(
            Math.max(...statisticsForTopic.map(stat => bloomLevelOrder[stat.currentBloomLevel])),
            maxBloom
        );

        return (highestBloomAchieved / maxBloom) * 100;
    };

    // Get the appropriate color for the progress bar
    const getProgressBarColor = (progress: number): string => {
        if (progress === 0) return 'bg-gray-400';
        if (progress <= 20) return 'bg-green-200';
        if (progress <= 40) return 'bg-green-400';
        if (progress <= 60) return 'bg-green-600';
        if (progress <= 80) return 'bg-green-700';
        return 'bg-green-800';
    };

    // Helper to get text representation of Bloom level
    const getBloomLevelText = (currentBloom: BloomLevel | null | undefined, maxBloom: BloomLevel | null | undefined): string => {
        const safeCurrentBloom = currentBloom ?? BloomLevel.NONE;
        const safeMaxBloom = maxBloom ?? BloomLevel.NONE;

        const currentText = safeCurrentBloom.charAt(0).toUpperCase() + safeCurrentBloom.slice(1).toLowerCase();
        const maxText = safeMaxBloom.charAt(0).toUpperCase() + safeMaxBloom.slice(1).toLowerCase();

        return `${currentText}/${maxText}`;
    };

    return (
        <section className="mb-8">
            {/* Flex container to place items side by side */}
            <div className="flex flex-row space-x-8">
                {/* Burndown Chart */}
                <div className="w-1/2">
                    <BurndownChartByStep
                        userId={userId}
                        learningPathId={learningPathId}
                        course={course}
                        learningStatistics={learningStatistics}
                    />
                </div>

                {/* Learning Progress for each topic */}
                <div className="w-1/2">
                    <h2 className="text-xl font-bold mb-4">Lernfortschritt</h2>
                    <div className="space-y-4">
                        {course.topics.map((topic) => {
                            const statisticsForTopic = learningStatistics.filter(stat => stat.topicId === topic.id);

                            // Ensure currentBloom doesn't exceed maxBloom
                            const currentBloomLevel = statisticsForTopic.length > 0
                                ? statisticsForTopic[statisticsForTopic.length - 1].currentBloomLevel
                                : BloomLevel.NONE;

                            // Map the currentBloomLevel and topic.maxBloom to their numeric equivalents
                            const currentBloom = bloomLevelOrder[currentBloomLevel];
                            const maxBloom = bloomLevelOrder[topic.maxBloom];

                            // Ensure current bloom doesn't exceed max bloom
                            const adjustedCurrentBloom = Math.min(currentBloom, maxBloom);

                            const progress = getMaxBloomLevelProgress(topic);
                            const progressBarColor = getProgressBarColor(progress);

                            return (
                                <div key={topic.id} className="flex flex-col">
                                    <div className="flex justify-between items-center mb-2">
                                        <span className="text-gray-800">{topic.name}</span>
                                    </div>
                                    <div className="relative w-full">
                                        <div className="w-full h-4 rounded-lg bg-gray-200">
                                            <div className={`h-4 rounded-lg ${progressBarColor}`}
                                                 style={{ width: `${progress}%` }}></div>
                                        </div>
                                        <div className="absolute inset-0 flex justify-center items-center">
                                            <span className="text-white text-sm font-medium">
                                                {getBloomLevelText(currentBloomLevel, topic.maxBloom)}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </section>
    );
}

export default LearningProgress;
