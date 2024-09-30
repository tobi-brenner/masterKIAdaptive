import {
    BookmarkIcon,
    ArrowDownIcon,
    ArrowDownTrayIcon,
    VideoCameraIcon,
    SpeakerWaveIcon
} from "@heroicons/react/24/outline";
import {BE_BASE_URL} from "@/constants";

interface CourseMaterialsProps {
    courseMaterials: { id: number; files: string[] } | undefined; // Adjusted to match your data structure
}

const removeUuidFromFilename = (filename: string) => {
    return filename.replace(/[_-][a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}(?=\.\w+$)/, '');
};

const CourseMaterials: React.FC<CourseMaterialsProps> = ({courseMaterials}) => {
    const baseUrl = `${BE_BASE_URL}/files/download`;
    if (!courseMaterials || !Array.isArray(courseMaterials.files)) {
        return <p className="text-gray-700">Keine Kursmaterialien verfügbar.</p>;
    }
    return (
        <section className="mb-8">
            <h2 className="text-xl font-bold mb-4">Kursmaterial</h2>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                {courseMaterials.files.map((material, index) => {
                    const displayName = removeUuidFromFilename(material);
                    return (
                        <div
                            key={index}
                            className="flex items-center justify-between p-3 border rounded-lg shadow-sm bg-white"
                        >
                            <span className="text-gray-800">{displayName}</span>
                            <a href={`${baseUrl}/${material}`} download>
                                <ArrowDownTrayIcon className="h-6 w-6 text-blue-500 hover:text-blue-700"/>
                            </a>
                            <a >
                                <VideoCameraIcon className="h-6 w-6 text-blue-500 hover:text-blue-700"/>
                            </a>
                            <a >
                                <SpeakerWaveIcon className="h-6 w-6 text-blue-500 hover:text-blue-700"/>
                            </a>

                        </div>
                    )
                })}
            </div>
        </section>
    )
        ;
};

export default CourseMaterials;
