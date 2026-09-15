import { useEffect, useState } from "react";
import {
    CheckCircle2,
    FileText,
    FolderOpen,
    RefreshCw,
    Trash2,
    Upload,
    XCircle,
} from "lucide-react";
import api from "../services/api";

const projectId = 1;

const initialForm = {
    name: "",
    type: "DRAWING",
    discipline: "ARCHITECTURE",
    description: "",
    filePath: "",
};

function Documents() {
    const [documents, setDocuments] = useState([]);
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [deletingId, setDeletingId] = useState(null);

    useEffect(() => {
        fetchDocuments();
    }, []);

    const fetchDocuments = async () => {
        try {
            setLoading(true);

            const response = await api.get(
                `/projects/${projectId}/documents`
            );

            setDocuments(response.data);
        } catch (requestError) {
            setError("Unable to load project documents.");
            console.error(requestError);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((currentForm) => ({
            ...currentForm,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setSubmitting(true);
        setMessage("");
        setError("");

        try {
            const response = await api.post(
                `/projects/${projectId}/documents`,
                form
            );

            setMessage(
                `${response.data.name} was processed successfully.`
            );

            setForm(initialForm);

            await fetchDocuments();
        } catch (requestError) {
            setError(
                requestError.response?.data?.message ||
                "Document processing failed. Check the file path and try again."
            );

            console.error(requestError);
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async (document) => {
        const confirmed = window.confirm(
            `Delete "${document.name}" from SuperBuilt?\n\n` +
            "Its document record and indexed AI chunks will be removed. " +
            "The original PDF file in sample-data will remain."
        );

        if (!confirmed) {
            return;
        }

        setDeletingId(document.id);
        setMessage("");
        setError("");

        try {
            await api.delete(
                `/projects/${projectId}/documents/${document.id}`
            );

            setMessage(`${document.name} was deleted successfully.`);

            await fetchDocuments();
        } catch (requestError) {
            setError(
                requestError.response?.data?.message ||
                "Unable to delete this document."
            );

            console.error(requestError);
        } finally {
            setDeletingId(null);
        }
    };

    const getStatusIcon = (status) => {
        if (status === "PROCESSED") {
            return <CheckCircle2 size={16} />;
        }

        if (status === "FAILED") {
            return <XCircle size={16} />;
        }

        return <RefreshCw size={16} />;
    };

    const formatDate = (value) => {
        if (!value) return "Unknown";

        return new Date(value).toLocaleString();
    };

    return (
        <div className="documents-page">
            <div className="page-header">
                <div>
                    <h1>Project Documents</h1>
                    <p>
                        Register a PDF and automatically extract, chunk, and index it.
                    </p>
                </div>

                <button
                    className="refresh-documents-btn"
                    onClick={fetchDocuments}
                    disabled={loading}
                >
                    <RefreshCw size={17} />
                    Refresh
                </button>
            </div>

            <div className="documents-grid">
                <section className="document-upload-card">
                    <div className="document-card-title">
                        <div className="document-upload-icon">
                            <Upload size={22} />
                        </div>

                        <div>
                            <h2>Add Document</h2>
                            <p>
                                The backend will process the PDF automatically after creation.
                            </p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit} className="document-form">
                        <label>
                            Document name
                            <input
                                name="name"
                                value={form.name}
                                onChange={handleChange}
                                placeholder="E-401 Electrical Drawing.pdf"
                                required
                            />
                        </label>

                        <div className="document-form-row">
                            <label>
                                Document type
                                <select
                                    name="type"
                                    value={form.type}
                                    onChange={handleChange}
                                >
                                    <option value="DRAWING">Drawing</option>
                                    <option value="SPECIFICATION">Specification</option>
                                    <option value="REPORT">Report</option>
                                    <option value="ELECTRICAL">Electrical</option>
                                    <option value="RFI_ATTACHMENT">RFI Attachment</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </label>

                            <label>
                                Discipline
                                <select
                                    name="discipline"
                                    value={form.discipline}
                                    onChange={handleChange}
                                >
                                    <option value="ARCHITECTURE">Architecture</option>
                                    <option value="STRUCTURAL">Structural</option>
                                    <option value="MEP">MEP</option>
                                    <option value="FIRE_SAFETY">Fire Safety</option>
                                    <option value="ELECTRICAL">Electrical</option>
                                    <option value="PLUMBING">Plumbing</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </label>
                        </div>

                        <label>
                            PDF file path
                            <div className="path-input">
                                <FolderOpen size={18} />
                                <input
                                    name="filePath"
                                    value={form.filePath}
                                    onChange={handleChange}
                                    placeholder="drawings/E-401.pdf"
                                    required
                                />
                            </div>
                            <small>
                                Path is relative to the backend’s configured
                                <code> sample-data </code> folder.
                            </small>
                        </label>

                        <label>
                            Description
                            <textarea
                                name="description"
                                value={form.description}
                                onChange={handleChange}
                                placeholder="Briefly describe this drawing or document..."
                                rows="4"
                            />
                        </label>

                        {message && (
                            <div className="document-message success">
                                <CheckCircle2 size={18} />
                                {message}
                            </div>
                        )}

                        {error && (
                            <div className="document-message error">
                                <XCircle size={18} />
                                {error}
                            </div>
                        )}

                        <button
                            className="process-document-btn"
                            type="submit"
                            disabled={submitting}
                        >
                            <Upload size={18} />
                            {submitting
                                ? "Processing document..."
                                : "Add and Process Document"}
                        </button>
                    </form>
                </section>

                <section className="document-list-card">
                    <div className="document-list-header">
                        <div>
                            <h2>Processed Documents</h2>
                            <p>{documents.length} document(s) in this project</p>
                        </div>
                    </div>

                    {loading ? (
                        <p className="document-empty-state">
                            Loading documents...
                        </p>
                    ) : documents.length === 0 ? (
                        <p className="document-empty-state">
                            No documents have been added yet.
                        </p>
                    ) : (
                        <div className="document-list">
                            {documents.map((document) => (
                                <article className="document-item" key={document.id}>
                                    <div className="document-file-icon">
                                        <FileText size={22} />
                                    </div>

                                    <div className="document-item-content">
                                        <div className="document-item-title-row">
                                            <h3>{document.name}</h3>

                                            <div className="document-item-actions">
        <span
            className={`document-status ${document.status.toLowerCase()}`}
        >
            {getStatusIcon(document.status)}
            {document.status}
        </span>

                                                <button
                                                    className="delete-document-btn"
                                                    onClick={() => handleDelete(document)}
                                                    disabled={deletingId === document.id}
                                                    title={`Delete ${document.name}`}
                                                >
                                                    <Trash2 size={16} />
                                                    {deletingId === document.id ? "Deleting..." : "Delete"}
                                                </button>
                                            </div>
                                        </div>

                                        <p>
                                            {document.description || "No description provided."}
                                        </p>

                                        <div className="document-meta">
                                            <span>{document.discipline.replace("_", " ")}</span>
                                            <span>{document.type.replace("_", " ")}</span>
                                            <span>{document.filePath}</span>
                                            <span>{formatDate(document.createdAt)}</span>
                                        </div>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}

export default Documents;