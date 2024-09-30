import { useEffect, useState } from 'react';
import { Line } from 'react-chartjs-2';
import { Course, LearningStatistics } from '@/app/lib/types/courses';
import { ChartData, ChartOptions } from 'chart.js';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

interface BurndownChartByStepProps {
    userId: string;
    learningPathId: string;
    course: Course;
    learningStatistics: LearningStatistics[];
}

const BurndownChartByStep: React.FC<BurndownChartByStepProps> = ({ course, learningStatistics }) => {
    const [chartData, setChartData] = useState<ChartData<'line'>>();
    const [chartOptions, setChartOptions] = useState<ChartOptions<'line'>>();
    console.log(learningStatistics)

    useEffect(() => {
        if (learningStatistics.length === 0) {
            return;
        }

        // Step 1: Calculate total points based on max bloom levels of the topics
        const totalBloomLevels = course.topics.reduce((total, topic) => {
            return total + bloomLevelToPoints(topic.maxBloom);
        }, 0);

        // Step 2: Group learning statistics into steps
        const steps: LearningStatistics[][] = [];
        let currentStep: LearningStatistics[] = [learningStatistics[0]];

        for (let i = 1; i < learningStatistics.length; i++) {
            const currentStat = learningStatistics[i];
            const prevStat = learningStatistics[i - 1];

            const currentTime = new Date(currentStat.recordedAt).getTime();
            const prevTime = new Date(prevStat.recordedAt).getTime();

            // Check if the difference is more than 1 minute (60000ms)
            if (currentTime - prevTime <= 50000) {
                currentStep.push(currentStat);
            } else {
                steps.push(currentStep);
                currentStep = [currentStat]; // Start a new step
            }
        }

        if (currentStep.length > 0) {
            steps.push(currentStep); // Push the final step
        }

        // Step 3: Calculate cumulative progress for each step, limiting to maxBloom if necessary
        const stepLabels = steps.map((_, index) => `Step ${index + 1}`);
        const stepProgress: number[] = [];
        let cumulativePoints = 0;

        steps.forEach(step => {
            const pointsAchieved = step.reduce((sum, stat) => {
                const topic = course.topics.find(t => t.id === stat.topicId);
                if (topic) {
                    const maxPoints = bloomLevelToPoints(topic.maxBloom);
                    const actualPoints = bloomLevelToPoints(stat.currentBloomLevel);
                    // Only add up to the maximum points for the topic (limit to maxBloom)
                    return sum + Math.min(actualPoints, maxPoints);
                }
                return sum;
            }, 0);

            cumulativePoints += pointsAchieved; // Accumulate the points achieved up to this step
            const remainingBloomLevels = totalBloomLevels - cumulativePoints;
            stepProgress.push(Math.max(remainingBloomLevels, 0)); // Ensure we don't go below 0
        });

        // Step 4: Prepare ideal progress line
        const idealLine = stepLabels.map((_, index) => {
            return totalBloomLevels - (totalBloomLevels / (stepLabels.length - 1)) * index;
        });

        // Step 5: Prepare chart data and options
        setChartData({
            labels: stepLabels,
            datasets: [
                {
                    label: 'Actual Progress',
                    data: stepProgress,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    fill: false,
                    tension: 0.1,
                },
                {
                    label: 'Ideal Progress',
                    data: idealLine,
                    borderColor: 'rgba(192, 75, 75, 1)',
                    fill: false,
                    borderDash: [10, 5], // Dashed line for the ideal progress
                    tension: 0.1,
                },
            ],
        });

        // Step 6: Adjust Y-axis to always show the total possible points
        setChartOptions({
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                },
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Lernschritte', // X-axis label
                    },
                },
                y: {
                    title: {
                        display: true,
                        text: 'Verbleibende Bloom-Level-Punkte', // Y-axis label
                    },
                    min: 0, // Ensure the Y-axis starts at 0
                    max: totalBloomLevels, // Set the Y-axis max to the total bloom levels
                },
            },
        });
    }, [course, learningStatistics]);

    return (
        <div>
            {chartData && chartOptions && <Line data={chartData} options={chartOptions} />}
        </div>
    );
};

const bloomLevelToPoints = (bloomLevel: string): number => {
    switch (bloomLevel) {
        case 'NONE':
            return 0;
        case 'REMEMBERING':
            return 1;
        case 'UNDERSTANDING':
            return 2;
        case 'APPLYING':
            return 3;
        case 'ANALYZING':
            return 4;
        case 'EVALUATING':
            return 5;
        case 'CREATING':
            return 6;
        default:
            return 0;
    }
};

export default BurndownChartByStep;