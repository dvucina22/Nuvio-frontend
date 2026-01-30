import { User } from "@/types/user/User";
import { Card } from "../ui/card";

interface UserCardProps {
    user: User;
}

const UserCard: React.FC<UserCardProps> = ({ user }) => {
    return (
        <Card className="flex flex-col justify-between w-full h-fit relative">
            <img src={user.profilePictureUrl ||  "/default-avatar.png"} alt="User Avatar" width={64} height={64} className="w-full h-[193px] rounded-t-[6px]"/>
                <div className="absolute top-2 left-2 flex gap-2 items-center rounded-[8px] bg-white/20 px-2 py-1">
                    <div className={`h-3 w-3 rounded-full ${user.isActive ? 'bg-green-500' : 'bg-red-500'}`}></div>
                    <p className="text-sm text-white">{user.isActive ? 'Active' : 'Inactive'}</p>
                </div>
            <div className="flex flex-col gap-[4px] px-3 py-2">
                <p className="text-lg font-medium">{user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : 'Unnamed user'}</p>
                <p className="text-sm text-muted-foreground">{user.roles.map(role => role.name).join(', ')}</p>
                <p className="text-sm text-muted-foreground">{user.email}</p>
            </div>
        </Card>
    );
}

export default UserCard;