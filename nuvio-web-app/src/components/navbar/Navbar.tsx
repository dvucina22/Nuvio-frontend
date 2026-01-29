"use client";

import { LogOut } from 'lucide-react';
import React, { useEffect, useState } from 'react';


const Navbar = () => {
    const [isMobile, setIsMobile] = useState(false);


    useEffect(() => {
        const checkScreen = () => setIsMobile(window.innerWidth < 1024);
        checkScreen();
        window.addEventListener("resize", checkScreen);
        return () => window.removeEventListener("resize", checkScreen);
    }, []);

    const logout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    }
    
    return (
        <div className='fixed z-50 max-w-[1441px] mx-auto mt-[26px] max-md:mt-0 w-full'>
            <div className='relative w-full max-lg:px-[10px] max-lg:justify-between min-h-[77px] max-md:mx-0 flex rounded-lg max-md:rounded-t-none max-md:rounded-b-lg 
            max-425:!min-h-[67px]'>
                    <a href="/dashboard">
                        <div style={{
                            backgroundImage: "url(logo192.png)"
                        }}  className='bg-no-repeat bg-contain min-h-[77px] w-[77px] mr-[12px] rounded-[18px] shadow-[0_4px_6px_rgba(0,0,0,0.1)]'/>
                    </a>
                <div className={`bg-[#EDF2F4] border-[#A2A9AD66] border w-full flex items-center justify-center font-mono rounded-[12px] text-[16px] shadow-[0_4px_6px_rgba(0,0,0,0.1)]`}>
                    <div className='flex gap-[12px] items-center'>
                        <div
                        onClick={() => window.location.href = "/dashboard"}
                        className='rounded-[6px]  px-3 py-1 hover:bg-[#5C6B7323] duration-200 ease-in-out cursor-pointer transition-all'>
                            Dashboard
                        </div>
                        <div
                        onClick={() => window.location.href = "/users"} 
                        className='rounded-[6px]  px-3 py-1 hover:bg-[#5C6B7323] duration-200 ease-in-out cursor-pointer transition-all'>
                            Users
                        </div>
                    </div>
                </div>
                <div className='max-lg:hidden w-[77px] flex-shrink-0 ml-[12px] flex items-center justify-center cursor-pointer hover:brightness-90 transition-all duration-200 ease-in-out'>
                    <div onClick={logout} className='rounded-[18px] h-[77px] w-[77px] bg-[#A63D3D] flex items-center justify-center shadow-[0_4px_6px_rgba(0,0,0,0.1)]'>
                        <LogOut color='white' size={32} />
                    </div>
                </div>                
            </div>
        </div>
    )
}

export default Navbar;