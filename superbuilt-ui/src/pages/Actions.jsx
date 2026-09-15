import { useEffect, useState } from "react";
import {
    CheckSquare,
    Search,
    User,
    Calendar,
    Flag,
    Link2,
} from "lucide-react";

import api from "../services/api";
function Actions() {
    const [actions, setActions] = useState([]);
    const [loading, setLoading] = useState(true);

    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [priorityFilter, setPriorityFilter] = useState("ALL");

    // const issueId = 1;
    const projectId = 1;

    useEffect(() => {
        fetchActions();

        const refreshInterval = setInterval(() => {
            fetchActions();
        }, 15000);

        return () => clearInterval(refreshInterval);
    }, []);
    const fetchActions = async () => {
        try {
            // Step 1: Get all issues for the project
            const issuesResponse = await api.get(
                `/projects/${projectId}/issues`
            );

            const issues = issuesResponse.data;

            // Step 2: Fetch actions for every issue
            const actionResponses = await Promise.all(
                issues.map((issue) =>
                    api.get(`/actions/issues/${issue.id}/actions`)
                )
            );

            // Step 3: Combine all actions into one array
            const allActions = actionResponses.flatMap(
                (response) => response.data
            );

            setActions(allActions);

        } catch (error) {
            console.error("Failed to fetch actions:", error);
        } finally {
            setLoading(false);
        }
    };

    const updateActionStatus = async (actionId, status) => {
        try {
            await api.patch(`/actions/${actionId}/status`, {
                status,
            });

            // Refresh actions after successful update
            fetchActions();

        } catch (error) {
            console.error("Failed to update action status:", error);
        }
    };

    const formatDate = (date) => {
        if (!date) return "No due date";

        return new Date(date).toLocaleDateString();
    };

    const filteredActions = actions.filter((action) => {
        const matchesSearch =
            action.title
                .toLowerCase()
                .includes(searchTerm.toLowerCase()) ||
            action.description
                .toLowerCase()
                .includes(searchTerm.toLowerCase());

        const matchesStatus =
            statusFilter === "ALL" ||
            action.status === statusFilter;

        const matchesPriority =
            priorityFilter === "ALL" ||
            action.priority === priorityFilter;

        return (
            matchesSearch &&
            matchesStatus &&
            matchesPriority
        );
    });

    if (loading) {
        return <h2>Loading actions...</h2>;
    }

    return (
        <div className="actions-page">

            {/* HEADER */}

            <div className="page-header">
                <div>
                    <h1>Actions</h1>

                    <p>
                        Track and manage actions generated for project issues.
                    </p>
                </div>

                <div className="issue-count">
                    {filteredActions.length} Actions
                </div>
            </div>

            {/* FILTERS */}

            <div className="action-filters">

                <div className="search-box">
                    <Search size={20} />

                    <input
                        type="text"
                        placeholder="Search actions..."
                        value={searchTerm}
                        onChange={(e) =>
                            setSearchTerm(e.target.value)
                        }
                    />
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
                        <option value="COMPLETED">
                            Completed
                        </option>
                    </select>
                </div>

                <div className="filter-box">
                    <select
                        value={priorityFilter}
                        onChange={(e) =>
                            setPriorityFilter(e.target.value)
                        }
                    >
                        <option value="ALL">All Priority</option>
                        <option value="HIGH">High Priority</option>
                        <option value="MEDIUM">Medium Priority</option>
                        <option value="LOW">Low Priority</option>
                    </select>
                </div>

            </div>

            {/* ACTION LIST */}

            <div className="actions-list">

                {filteredActions.map((action) => (

                    <div
                        className="action-card"
                        key={action.id}
                    >

                        <div className="action-card-header">

                            <div className="action-title-section">

                                <div className="action-icon">
                                    <CheckSquare size={22} />
                                </div>

                                <div>

                                    <h3>{action.title}</h3>

                                    <div className="linked-issue">
                                        <Link2 size={14} />

                                        Issue #{action.issue?.id}:{" "}
                                        {action.issue?.title}
                                    </div>

                                </div>

                            </div>

                            <div className="action-badges">

                <span
                    className={`priority-badge ${action.priority.toLowerCase()}`}
                >
                  <Flag size={13} />

                    {action.priority}
                </span>

                                <span
                                    className={`action-status-badge ${action.status.toLowerCase()}`}
                                >
                  {action.status.replace("_", " ")}
                </span>

                            </div>

                        </div>

                        {/* DESCRIPTION */}

                        <p className="action-description">
                            {action.description}
                        </p>

                        {/* META */}

                        <div className="action-meta">

                            <div className="meta-item">
                                <User size={17} />

                                <span>
                  Assigned to:
                </span>

                                <strong>
                                    {action.assignee || "Unassigned"}
                                </strong>
                            </div>

                            <div className="action-buttons">

                                {action.status === "OPEN" && (
                                    <button
                                        className="start-action-btn"
                                        onClick={() =>
                                            updateActionStatus(
                                                action.id,
                                                "IN_PROGRESS"
                                            )
                                        }
                                    >
                                        ▶ Start Action
                                    </button>
                                )}

                                {action.status === "IN_PROGRESS" && (
                                    <button
                                        className="complete-action-btn"
                                        onClick={() =>
                                            updateActionStatus(
                                                action.id,
                                                "COMPLETED"
                                            )
                                        }
                                    >
                                        ✓ Mark Completed
                                    </button>
                                )}

                                {action.status === "COMPLETED" && (
                                    <div className="completed-action">
                                        ✓ Action Completed
                                    </div>
                                )}

                            </div>

                            <div className="meta-item">
                                <Calendar size={17} />

                                {formatDate(action.dueDate)}
                            </div>

                            {action.completedAt && (
                                <div className="completed-date">
                                    ✓ Completed on{" "}
                                    {formatDate(action.completedAt)}
                                </div>
                            )}

                        </div>

                    </div>

                ))}

            </div>

            {filteredActions.length === 0 && (
                <div className="empty-state">
                    No actions found.
                </div>
            )}

        </div>
    );
}

export default Actions;