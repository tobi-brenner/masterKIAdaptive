
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

interface BurndownChartProps {
    userId: string;
    learningPathId: string;
    course: Course;
    learningStatistics: LearningStatistics[];
}

const BurndownChart: React.FC<BurndownChartProps> = ({ course, learningStatistics }) => {
    const [chartData, setChartData] = useState<ChartData<'line'>>();
    const [chartOptions, setChartOptions] = useState<ChartOptions<'line'>>();
    console.log(learningStatistics, "LEARNSTATS")

    useEffect(() => {
        // Step 1: Calculate the total points to achieve based on max bloom levels
        const totalBloomLevels = course.topics.reduce((total, topic) => {
            return total + bloomLevelToPoints(topic.maxBloom);
        }, 0);

        // Step 2: Determine the start and end dates of the course
        if (learningStatistics.length === 0) {
            return;
        }

        const startDate = new Date(learningStatistics[0].recordedAt); // Starting date based on the first learning statistic
        const periodLength = course.courseSettings.periodLength;
        const periodUnit = course.courseSettings.periodUnit;

        const endDate = new Date(startDate);
        switch (periodUnit) {
            case 'DAYS':
                endDate.setDate(endDate.getDate() + periodLength);
                break;
            case 'WEEKS':
                endDate.setDate(endDate.getDate() + periodLength * 7);
                break;
            case 'MONTHS':
                endDate.setMonth(endDate.getMonth() + periodLength);
                break;
            default:
                endDate.setDate(endDate.getDate() + periodLength); // Default to days if unit is unknown
                break;
        }

        // Step 3: Create an array of all dates from start to end date
        const labels: any[] = [];
        let currentDate = new Date(startDate);
        while (currentDate <= endDate) {
            labels.push(currentDate.toISOString().split('T')[0]); // Add date as 'YYYY-MM-DD'
            currentDate.setDate(currentDate.getDate() + 1); // Increment by one day
        }

        // Step 4: Initialize data structure for chart data
        const dailyProgress: Record<string, number> = {}; // { date: remainingBloomLevels }
        let remainingBloomLevels = totalBloomLevels;

        // Step 5: Group learning statistics by day and calculate daily remaining bloom levels
        learningStatistics.forEach(stat => {
            const date = new Date(stat.recordedAt).toISOString().split('T')[0]; // Use only the date part
            const bloomPointsAchieved = bloomLevelToPoints(stat.currentBloomLevel);

            // Only subtract achieved points if the date is within the course duration
            if (labels.includes(date)) {
                if (!dailyProgress[date]) {
                    dailyProgress[date] = remainingBloomLevels;
                }

                // Subtract achieved points from the total for this date
                dailyProgress[date] -= bloomPointsAchieved;
                remainingBloomLevels -= bloomPointsAchieved;
            }
        });

        // Step 6: Prepare chart data and options
        const data = labels.map(date => dailyProgress[date] || remainingBloomLevels); // Use remaining levels if no data

        const idealLine = labels.map((_, index) => totalBloomLevels - (totalBloomLevels / (labels.length - 1)) * index);

        setChartData({
            labels,
            datasets: [
                {
                    label: 'Actual Progress',
                    data,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    fill: false,
                    tension: 0.1,
                },
                {
                    label: 'Ideal Progress',
                    data: idealLine,
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderDash: [5, 5],
                    fill: false,
                    tension: 0.1,
                },
            ],
        });

        setChartOptions({
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                },
            },
        });
    }, [course, learningStatistics]);

    return (
        <div>
            <h2 className="text-xl font-bold mb-4">Burndown Chart</h2>
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

export default BurndownChart;
