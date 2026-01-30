import { useGetUsers } from "@/api/hooks/user/useGetUsers";
import UserCard from "@/components/user/UserCard";
import { User } from "@/types/user/User";
import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { gsap } from "gsap";

const UsersPage: React.FC = () => {
    const { getAllUsers, loading: loadingUsers, error: errorUsers } = useGetUsers();
    const [users, setUsers] = useState<User[]>([]);
    const containerRef = useRef<HTMLDivElement | null>(null);

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

    useLayoutEffect(() => {
        if (!containerRef.current || loadingUsers || users.length === 0) return;

        const ctx = gsap.context(() => {
            const cards = gsap.utils.toArray('[data-user-card]');
            
            gsap.set(cards, { y: 50, autoAlpha: 0 });

            gsap.to(cards, {
                y: 0,
                autoAlpha: 1,
                duration: 0.6,
                stagger: 0.1,
                ease: "power3.out",
            });
        }, containerRef);

        return () => ctx.revert();
    }, [users, loadingUsers]);

    useLayoutEffect(() => {
        if (!containerRef.current || loadingUsers || users.length === 0) return;

        const ctx = gsap.context(() => {
            const cards = gsap.utils.toArray('[data-user-card]');

            cards.forEach((card) => {
                const element = card as HTMLElement;
                
                element.addEventListener('mouseenter', () => {
                    gsap.to(element, {
                        y: -8,
                        scale: 1.02,
                        duration: 0.3,
                        ease: "power2.out",
                    });
                });

                element.addEventListener('mouseleave', () => {
                    gsap.to(element, {
                        y: 0,
                        scale: 1,
                        duration: 0.3,
                        ease: "power2.out",
                    });
                });
            });
        }, containerRef);

        return () => ctx.revert();
    }, [users, loadingUsers]);
    
    return (
        <div 
            ref={containerRef}
            className="mt-[120px] grid grid-cols-7 gap-[12px] h-fit max-w-[1441px] w-full"
        >
            {loadingUsers && Array.from({ length: 14 }).map((_, index) => (
                <div key={index} className="w-full h-[193px] bg-gray-200 animate-pulse rounded-md" />
            ))}
            {!loadingUsers && users && users.map((user) => (
                <div key={user.id} data-user-card>
                    <UserCard user={user} />
                </div>
            ))}
        </div>
    )
}

export default UsersPage;