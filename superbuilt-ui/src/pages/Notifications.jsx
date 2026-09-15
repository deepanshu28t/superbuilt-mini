import { useEffect, useState } from "react";
import {
    CheckCircle2,
    Mail,
    RefreshCw,
    Send,
    XCircle,
} from "lucide-react";
import api from "../services/api";

const projectId = 1;

function Notifications() {
    const [logs, setLogs] = useState([]);
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchNotifications();
    }, []);

    const fetchNotifications = async () => {
        try {
            setLoading(true);

            const [logsResponse, summaryResponse] = await Promise.all([
                api.get(`/projects/${projectId}/mail-logs`),
                api.get(`/projects/${projectId}/mail-logs/summary`),
            ]);

            setLogs(logsResponse.data);
            setSummary(summaryResponse.data);
        } catch (error) {
            console.error("Unable to load mail history.", error);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (value) => {
        if (!value) return "Unknown";

        return new Date(value).toLocaleString();
    };

    if (loading) {
        return <h2>Loading mail history...</h2>;
    }

    return (
        <div className="notifications-page">
            <div className="page-header">
                <div>
                    <h1>Notifications</h1>
                    <p>Track all automated project emails and delivery outcomes.</p>
                </div>

                <button
                    className="refresh-documents-btn"
                    onClick={fetchNotifications}
                >
                    <RefreshCw size={17} />
                    Refresh
                </button>
            </div>

            <div className="notification-summary-grid">
                <div className="notification-stat-card">
                    <Mail size={24} />
                    <div>
                        <p>Total Emails</p>
                        <h2>{summary?.totalEmails || 0}</h2>
                    </div>
                </div>

                <div className="notification-stat-card sent">
                    <CheckCircle2 size={24} />
                    <div>
                        <p>Emails Sent</p>
                        <h2>{summary?.emailsSent || 0}</h2>
                    </div>
                </div>

                <div className="notification-stat-card brief">
                    <Send size={24} />
                    <div>
                        <p>Morning Briefs</p>
                        <h2>{summary?.morningBriefsSent || 0}</h2>
                    </div>
                </div>

                <div className="notification-stat-card failed">
                    <XCircle size={24} />
                    <div>
                        <p>Failed Emails</p>
                        <h2>{summary?.failedEmails || 0}</h2>
                    </div>
                </div>
            </div>

            <section className="notification-list-card">
                <div className="document-list-header">
                    <div>
                        <h2>Email History</h2>
                        <p>Latest 100 email delivery attempts</p>
                    </div>
                </div>

                {logs.length === 0 ? (
                    <p className="document-empty-state">
                        No emails have been sent yet.
                    </p>
                ) : (
                    <div className="notification-list">
                        {logs.map((log) => (
                            <article className="notification-item" key={log.id}>
                                <div
                                    className={`notification-icon ${log.status.toLowerCase()}`}
                                >
                                    {log.status === "SENT" ? (
                                        <CheckCircle2 size={21} />
                                    ) : (
                                        <XCircle size={21} />
                                    )}
                                </div>

                                <div className="notification-content">
                                    <div className="notification-title-row">
                                        <h3>{log.subject}</h3>

                                        <span
                                            className={`document-status ${log.status.toLowerCase()}`}
                                        >
                      {log.status}
                    </span>
                                    </div>

                                    <p>
                                        To: <strong>{log.recipient}</strong>
                                    </p>

                                    <div className="document-meta">
                                        <span>{log.type.replace("_", " ")}</span>
                                        <span>{formatDate(log.sentAt)}</span>
                                    </div>

                                    {log.errorMessage && (
                                        <p className="notification-error">
                                            Error: {log.errorMessage}
                                        </p>
                                    )}
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}

export default Notifications;