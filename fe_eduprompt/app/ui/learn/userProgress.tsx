// "use client";
// import { useEffect, useState } from "react";
//
// interface TopicProgress {
//     topicName: string;
//     bloomLevel: number; // Assuming this is from 0 to 5 for each level
// }
//
// interface UserProgressProps {
//     topicsProgress: TopicProgress[] | any;
// }
//
// const UserProgress: React.FC<UserProgressProps> = ({ topicsProgress }) => {
//     const [chartData, setChartData] = useState<number[]>([]);
//
//     useEffect(() => {
//         // This effect could set up the data for the burndown chart
//         setChartData(topicsProgress.map((topic: any) => topic.bloomLevel));
//     }, [topicsProgress]);
//
//     return (
//         <section className="mb-8">
//             <h2 className="text-xl font-bold mb-4">Benutzerfortschritt</h2>
//
//             {/* Placeholder for Burndown Chart */}
//             <div className="mb-4 p-4 border rounded-lg shadow-sm bg-white">
//                 <h3 className="text-lg font-semibold mb-2">Fortschrittsdiagramm</h3>
//                 <p className="text-sm text-gray-600">Burndown chart goes here</p>
//             </div>
//
//             {/* Topic Progress with Bloom Levels */}
//             <div>
//                 {topicsProgress.map((topic, index) => (
//                     <div key={index} className="mb-4">
//                         <h4 className="text-md font-semibold mb-2">{topic.topicName}</h4>
//                         <div className="w-full bg-gray-200 rounded-full h-4">
//                             <div
//                                 className="bg-blue-600 h-4 rounded-full"
//                                 style={{ width: `${(topic.bloomLevel / 5) * 100}%` }}
//                             ></div>
//                         </div>
//                     </div>
//                 ))}
//             </div>
//         </section>
//     );
// };
//
// export default UserProgress;
