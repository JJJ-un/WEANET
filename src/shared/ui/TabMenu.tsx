import { Link } from "@tanstack/react-router";
import HomeIcon from "@/shared/assets/icons/home.svg?react";
import MapIcon from "@/shared/assets/icons/map.svg?react";
import CommunityIcon from "@/shared/assets/icons/community.svg?react";
import ProfileIcon from "@/shared/assets/icons/profile.svg?react";

const Tab = [
  { to: "/", label: "홈", icon: HomeIcon },
  { to: "/map", label: "지도", icon: MapIcon },
  { to: "/community", label: "커뮤니티", icon: CommunityIcon },
  { to: "/my", label: "My", icon: ProfileIcon },
];

const TavMenu = () => {
  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 max-w-[39rem] w-full flex justify-around items-center py-4">
      {Tab.map((tab) => (
        <Link
          key={tab.to}
          to={tab.to}
          className="flex flex-col justify-center items-center gap-2"
        >
          <tab.icon />
          <span className="text-muted-foreground text-xs">{tab.label}</span>
        </Link>
      ))}
    </nav>
  );
};

export default TavMenu;
