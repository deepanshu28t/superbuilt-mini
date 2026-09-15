import { NavLink } from "react-router-dom";
import {
    LayoutDashboard,
    FileText,
    AlertTriangle,
    CheckSquare,
    Search,
    Mail,
} from "lucide-react";

function Sidebar() {
    return (
        <aside className="sidebar">
            <div className="logo">
                <h2>SuperBuilt AI</h2>
                <span>Construction Intelligence</span>
            </div>

            <nav className="nav-links">
                <NavLink to="/" end>
                    <LayoutDashboard size={20} />
                    Dashboard
                </NavLink>

                <NavLink to="/documents">
                    <FileText size={20} />
                    Documents
                </NavLink>

                <NavLink to="/issues">
                    <AlertTriangle size={20} />
                    Issues
                </NavLink>

                <NavLink to="/actions">
                    <CheckSquare size={20} />
                    Actions
                </NavLink>

                <NavLink to="/search">
                    <Search size={20} />
                    AI Search
                </NavLink>

                <NavLink to="/notifications">
                    <Mail size={20} />
                    Notifications
                </NavLink>
            </nav>
        </aside>
    );
}

export default Sidebar;