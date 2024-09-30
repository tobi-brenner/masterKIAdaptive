import {
    BanknotesIcon,
    ClockIcon,
    UserGroupIcon,
    InboxIcon,
} from '@heroicons/react/24/outline';
import {lusitana} from '@/app/ui/fonts';
import {Topic} from "@/app/lib/types/courses";
import {User} from "@/app/lib/types/users";
import Link from "next/link";
import {auth} from "@/auth";
import {fetchLearningPaths} from "@/app/lib/api/learningpath/learningpath";

const iconMap = {
    collected: BanknotesIcon,
    customers: UserGroupIcon,
    pending: ClockIcon,
    invoices: InboxIcon,
};

interface CardProps {
    id: string;
    courseId: any;
    subject: string;
    description: string;
    topics: Topic[];
    prof: User;
    currentUserId: string | undefined;
}

interface WrapperProps {
    userId?: string | undefined
}

export default async function PathCardWrapper({userId}: WrapperProps) {
    const paths = await fetchLearningPaths(userId);
    const session = await auth();

    if (paths.length === 0) {
        return <p>No learning paths exist yet.</p>;
    }
    console.log(paths, "paths PATHCARDWRAPPER");

    return (
        <>
            {paths.map((path: {
                id: string | any;
                course: any;
                learningSteps: any;
                // subject: any;
                // description: any;
                // topics: any;
                // prof: User;
                // users: any;
            }) => (
                <Card
                    key={path.id}
                    id={path.id}
                    courseId={path.course.id}
                    subject={path.course.subject}
                    description={path.course.description}
                    topics={path.course.topics}
                    prof={path.course.prof}
                    currentUserId={session?.user.id}
                />
            ))}
        </>
    );
}

export function Card({id, courseId, subject, description, topics, prof, currentUserId}: CardProps) {
    const Icon = iconMap["collected"];
    return (
        <div>
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">

                <Link href={`/learn/path/${id}?courseId=${courseId}`} passHref>

                    <div className="flex flex-col justify-between p-4">
                        <div className="flex p-4">
                            {Icon && <Icon className="h-5 w-5 text-gray-700"/>}
                            <h3 className="ml-2 text-2xl font-medium">{subject}</h3>
                        </div>
                        <p className="ml-auto text-sm font-light" style={{alignSelf: 'flex-end'}}>{prof.name}</p>
                    </div>

                    <div className="card-content">
                        <p className={` rounded-xl bg-white px-4 py-8 text-center text-sm`}>
                            {description}
                        </p>
                    </div>
                </Link>
            </div>
        </div>
    );
}
