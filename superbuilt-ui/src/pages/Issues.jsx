import { useEffect, useState } from "react";
import {
    AlertTriangle,
    Search,
    Brain,
    Calendar,
    Filter,
    Save,
} from "lucide-react";

import api from "../services/api";

function Issues() {
    const [issues, setIssues] = useState([]);
    const [loading, setLoading] = useState(true);

    const [searchTerm, setSearchTerm] = useState("");
    const [severityFilter, setSeverityFilter] = useState("ALL");
    const [statusFilter, setStatusFilter] = useState("ALL");

    // Stores the selected status for each issue
    const [selectedStatuses, setSelectedStatuses] = useState({});

    // Stores which issue is currently being updated
    const [updatingIssueId, setUpdatingIssueId] = useState(null);

    const projectId = 1;

    useEffect(() => {
        fetchIssues();

        const refreshInterval = setInterval(() => {
            fetchIssues();
        }, 15000);

        return () => clearInterval(refreshInterval);
    }, []);

    const fetchIssues = async () => {
        try {
            const response = await api.get(
                `/projects/${projectId}/issues`
            );

            setIssues(response.data);

            // Initialize dropdown values with current issue status
            const statusMap = {};

            response.data.forEach((issue) => {
                statusMap[issue.id] = issue.status;
            });

            setSelectedStatuses(statusMap);

        } catch (error) {
            console.error("Failed to fetch issues:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = (issueId, newStatus) => {
        setSelectedStatuses((previous) => ({
            ...previous,
            [issueId]: newStatus,
        }));
    };

    const updateIssueStatus = async (issueId) => {
        try {
            setUpdatingIssueId(issueId);

            const newStatus = selectedStatuses[issueId];

            const response = await api.patch(
                `/projects/${projectId}/issues/${issueId}/status`,
                {
                    status: newStatus,
                }
            );

            // Update the issue directly in the UI
            setIssues((previousIssues) =>
                previousIssues.map((issue) =>
                    issue.id === issueId
                        ? response.data
                        : issue
                )
            );

            alert("Issue status updated successfully!");

        } catch (error) {
            console.error(
                "Failed to update issue status:",
                error
            );

            alert("Failed to update issue status.");

        } finally {
            setUpdatingIssueId(null);
        }
    };

    const filteredIssues = issues.filter((issue) => {
        const matchesSearch =
            issue.title
                .toLowerCase()
                .includes(searchTerm.toLowerCase()) ||
            issue.description
                .toLowerCase()
                .includes(searchTerm.toLowerCase());

        const matchesSeverity =
            severityFilter === "ALL" ||
            issue.severity === severityFilter;

        const matchesStatus =
            statusFilter === "ALL" ||
            issue.status === statusFilter;

        return (
            matchesSearch &&
            matchesSeverity &&
            matchesStatus
        );
    });

    const formatDate = (date) => {
        if (!date) return "No due date";

        return new Date(date).toLocaleDateString();
    };

    if (loading) {
        return <h2>Loading issues...</h2>;
    }

    return (
        <div className="issues-page">

            <div className="page-header">
                <div>
                    <h1>Issues</h1>
                    <p>
                        Monitor and manage AI-detected project issues.
                    </p>
                </div>

                <div className="issue-count">
                    {filteredIssues.length} Issues
                </div>
            </div>

            {/* FILTERS */}

            <div className="issue-filters">

                <div className="search-box">
                    <Search size={20} />

                    <input
                        type="text"
                        placeholder="Search issues..."
                        value={searchTerm}
                        onChange={(e) =>
                            setSearchTerm(e.target.value)
                        }
                    />
                </div>

                <div className="filter-box">
                    <Filter size={18} />

                    <select
                        value={severityFilter}
                        onChange={(e) =>
                            setSeverityFilter(e.target.value)
                        }
                    >
                        <option value="ALL">All Severity</option>
                        <option value="CRITICAL">Critical</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                </div>

                <div className="filter-box">
                    <select
                        value={statusFilter}
                        onChange={(e) =>
                            setStatusFilter(e.target.value)
                        }
                    >
                        <option value="ALL">All Status</option>
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">
                            In Progress
                        </option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                    </select>
                </div>

            </div>

            {/* ISSUES */}

            <div className="issues-list">

                {filteredIssues.map((issue) => (

                    <div className="issue-card" key={issue.id}>

                        <div className="issue-card-header">

                            <div className="issue-title-section">

                                <div className="issue-icon">
                                    <AlertTriangle size={22} />
                                </div>

                                <div>
                                    <h3>{issue.title}</h3>

                                    <span className="issue-type">
                    {issue.type}
                  </span>
                                </div>

                            </div>

                            <div className="issue-badges">

                <span
                    className={`severity-badge ${issue.severity.toLowerCase()}`}
                >
                  {issue.severity}
                </span>

                                <span
                                    className={`status-badge ${issue.status.toLowerCase()}`}
                                >
                  {issue.status.replace("_", " ")}
                </span>

                            </div>

                        </div>

                        <p className="issue-description">
                            {issue.description}
                        </p>

                        <div className="issue-meta">

                            <div className="meta-item">
                                <Brain size={17} />

                                AI Confidence:
                                <strong>
                                    {Math.round(
                                        issue.confidenceScore * 100
                                    )}%
                                </strong>
                            </div>

                            <div className="meta-item">
                                ⚠ Risk:
                                <strong>{issue.riskScore}</strong>
                            </div>

                            <div className="meta-item">
                                <Calendar size={17} />
                                {formatDate(issue.dueDate)}
                            </div>

                            {issue.requiresDecision && (
                                <div className="decision-badge">
                                    Decision Required
                                </div>
                            )}

                        </div>

                        {/* STATUS UPDATE */}

                        <div className="issue-actions">

                            <div className="status-selector">

                                <label>Update Status</label>

                                <select
                                    value={selectedStatuses[issue.id] || issue.status}
                                    onChange={(e) =>
                                        handleStatusChange(
                                            issue.id,
                                            e.target.value
                                        )
                                    }
                                >
                                    <option value="OPEN">Open</option>

                                    <option value="IN_PROGRESS">
                                        In Progress
                                    </option>

                                    <option value="RESOLVED">
                                        Resolved
                                    </option>

                                    <option value="CLOSED">
                                        Closed
                                    </option>

                                </select>

                            </div>

                            <button
                                className="update-status-btn"
                                onClick={() => updateIssueStatus(issue.id)}
                                disabled={
                                    updatingIssueId === issue.id ||
                                    selectedStatuses[issue.id] === issue.status
                                }
                            >
                                <Save size={17} />

                                {updatingIssueId === issue.id
                                    ? "Updating..."
                                    : "Save Changes"}

                            </button>

                        </div>

                    </div>

                ))}

            </div>

            {filteredIssues.length === 0 && (
                <div className="empty-state">
                    No issues found.
                </div>
            )}

        </div>
    );
}

export default Issues;