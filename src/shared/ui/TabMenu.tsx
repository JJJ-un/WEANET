import { Link } from "@tanstack/react-router";

const Tab = [
  { to: "/", label: "홈" },
  { to: "/map", label: "지도" },
  { to: "/community", label: "커뮤니티" },
  { to: "/my", label: "My" },
];

const TavMenu = () => {
  return (
    <nav>
      <div className="bg-background">
        {Tab.map((tab) => (
          <Link key={tab.to} to={tab.to}>
            {tab.label}
          </Link>
        ))}
      </div>
    </nav>
  );
};

export default TavMenu;
