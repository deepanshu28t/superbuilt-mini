import { useEffect, useState } from "react";
import {
    FileText,
    CheckCircle,
    AlertTriangle,
    CircleAlert,
    Flame,
    ClipboardList,
    ListChecks,
} from "lucide-react";

import api from "../services/api";

function Dashboard() {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);

    const projectId = 1;

    useEffect(() => {
        fetchDashboard();
    }, []);

    const fetchDashboard = async () => {
        try {
            const response = await api.get(
                `/projects/${projectId}/dashboard`
            );

            setDashboard(response.data);
        } catch (error) {
            console.error("Failed to fetch dashboard", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <h2>Loading dashboard...</h2>;
    }

    if (!dashboard) {
        return <h2>Failed to load dashboard.</h2>;
    }

    const cards = [
        {
            title: "Total Documents",
            value: dashboard.totalDocuments,
            icon: <FileText size={28} />,
        },
        {
            title: "Processed Documents",
            value: dashboard.processedDocuments,
            icon: <CheckCircle size={28} />,
        },
        {
            title: "Total Issues",
            value: dashboard.totalIssues,
            icon: <AlertTriangle size={28} />,
        },
        {
            title: "Open Issues",
            value: dashboard.openIssues,
            icon: <CircleAlert size={28} />,
        },
        {
            title: "High Severity Issues",
            value: dashboard.highSeverityIssues,
            icon: <Flame size={28} />,
        },
        {
            title: "Decision Required",
            value: dashboard.decisionRequiredIssues,
            icon: <ClipboardList size={28} />,
        },
        {
            title: "Pending Actions",
            value: dashboard.pendingActions,
            icon: <ListChecks size={28} />,
        },
    ];

    return (
        <div className="dashboard">
            <div className="dashboard-header">
                <div>
                    <h1>Project Dashboard</h1>
                    <p>AI-powered construction project intelligence.</p>
                </div>
            </div>

            <div className="dashboard-grid">
                {cards.map((card) => (
                    <div className="stat-card" key={card.title}>
                        <div className="stat-icon">{card.icon}</div>

                        <div>
                            <p>{card.title}</p>
                            <h2>{card.value}</h2>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Dashboard;