import { useGetUsers } from "@/api/hooks/user/useGetUsers";
import UserCard from "@/components/user/UserCard";
import { User } from "@/types/user/User";
import { useEffect, useState } from "react";

const UsersPage: React.FC = () => {
    const { getAllUsers, loading: loadingUsers, error: errorUsers } = useGetUsers();
    const [users, setUsers] = useState<User[]>([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const users = await getAllUsers();
                console.log("Fetched users:", users);
                setUsers(users);
            } catch (err) {
                console.error("Error fetching users:", err);
            }
        };

        fetchUsers();
    }, []);
    
    return (
        <div className="mt-[120px] flex gap-[12px] h-fit max-w-[1441px] w-full">
            {loadingUsers && Array.from({ length: 5 }).map((_, index) => (
                <div key={index} className="w-full h-24 bg-gray-200 animate-pulse rounded-md" />
            ))}
            {!loadingUsers && users && users.map((user) => (
                <UserCard key={user.id} user={user} />
            ))}
        </div>
    )
}

export default UsersPage;