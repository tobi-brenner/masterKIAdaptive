
'use client';
import React, { useEffect, useState } from 'react';
import { Line } from 'react-chartjs-2';
import { ChartData, ChartOptions, TooltipItem } from 'chart.js';
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
import { Course, ExtendedLearningStatistics } from '@/app/lib/types/courses';
import { User } from '@/app/lib/types/users';
import Link from "next/link";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface AggregatedBurndownChartProps {
    course: Course;
    learningStatistics: ExtendedLearningStatistics[];
    users: User[];
}

const AggregatedBurndownChart: React.FC<AggregatedBurndownChartProps> = ({ course, learningStatistics, users }) => {
    const [chartData, setChartData] = useState<ChartData<'line'>>();
    const [chartOptions, setChartOptions] = useState<ChartOptions<'line'>>();
    const [showAverage, setShowAverage] = useState<boolean>(false);

    useEffect(() => {
        // Step 1: Calculate the total points to achieve based on max bloom levels
        const totalBloomLevels = course.topics.reduce((total, topic) => {
            return total + bloomLevelToPoints(topic.maxBloom);
        }, 0);

        // Prepare data structures for progress tracking
        const userProgress: Record<string, Record<string, number>> = {};

        // Step 2: Populate user progress for each user
        users.forEach((user) => {
            userProgress[user.id!] = {};
        });

        learningStatistics.forEach((stat) => {
            const date = new Date(stat.recordedAt).toISOString().split('T')[0]; // Use only the date part
            const bloomPointsAchieved = bloomLevelToPoints(stat.currentBloomLevel);

            if (!userProgress[stat.user.id!][date]) {
                userProgress[stat.user.id!][date] = totalBloomLevels;
            }

            userProgress[stat.user.id!][date] -= bloomPointsAchieved;
        });

        // Step 3: Calculate average progress
        const dates = Array.from(new Set(Object.values(userProgress).flatMap((progress) => Object.keys(progress)))).sort();
        const averageProgress = dates.map((date) => {
            let sum = 0;
            let count = 0;
            Object.keys(userProgress).forEach((userId) => {
                if (userProgress[userId][date] !== undefined) {
                    sum += userProgress[userId][date];
                    count++;
                }
            });
            return count > 0 ? sum / count : 0;
        });

        // Step 4: Prepare chart data for all users or average line
        const datasets = showAverage
            ? [
                {
                    label: 'Average Progress',
                    data: averageProgress,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    fill: false,
                    tension: 0.1,
                },
            ]
            : users.map((user) => ({
                label: `${user.firstName} ${user.lastName}`,
                data: dates.map((date) => userProgress[user.id!][date] || totalBloomLevels),
                borderColor: getRandomColor(),
                fill: false,
                tension: 0.1,
            }));

        // Prepare ideal line for comparison
        const idealLine = dates.map((_, index) => totalBloomLevels - (totalBloomLevels / dates.length) * index);

        setChartData({
            labels: dates,
            datasets: [
                ...datasets,
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
                tooltip: {
                    callbacks: {
                        label: function (tooltipItem: TooltipItem<'line'>) {
                            return `${tooltipItem.dataset.label}: ${tooltipItem.raw}`;
                        },
                    },
                },
            },
        });
    }, [course, learningStatistics, users, showAverage]);

    // Helper function to convert bloom levels to points
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

    // Helper function to generate random colors for each user line
    const getRandomColor = (): string => {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    };

    return (
        <div>
            <div className="mb-4">
                <button
                    onClick={() => setShowAverage(!showAverage)}
                    className="px-4 py-2 font-medium text-white bg-blue-500 rounded"
                >
                    {showAverage ? 'Show Individual Progress' : 'Show Average Progress'}
                </button>
            </div>
            {/*<h2 className="text-xl font-bold mb-4">Aggregated Burndown Chart</h2>*/}
            {chartData && chartOptions && <Line data={chartData} options={chartOptions} />}
        </div>
    );
};

export default AggregatedBurndownChart;
