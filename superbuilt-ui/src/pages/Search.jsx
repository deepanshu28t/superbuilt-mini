import { useState } from "react";
import { Search as SearchIcon, FileText, Sparkles, AlertCircle } from "lucide-react";
import api from "../services/api";
import "./Search.css";

const PROJECT_ID = 1;

const exampleQueries = [
    "HVAC beam conflict",
    "fire safety requirements",
    "electrical coordination issues",
    "structural design requirements",
];

function Search() {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [searchedQuery, setSearchedQuery] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const performSearch = async (searchQuery = query) => {
        const trimmedQuery = searchQuery.trim();

        if (!trimmedQuery) {
            setResults([]);
            setSearchedQuery("");
            return;
        }

        try {
            setLoading(true);
            setError("");

            const response = await api.get(
                `/projects/${PROJECT_ID}/search`,
                {
                    params: {
                        query: trimmedQuery,
                        limit: 1,
                    },
                }
            );

            setResults(response.data);
            setSearchedQuery(trimmedQuery);
        } catch (err) {
            console.error("Semantic search failed:", err);
            setError(
                "Unable to search project documents. Please make sure the backend is running."
            );
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        performSearch();
    };

    const handleExampleClick = (example) => {
        setQuery(example);
        performSearch(example);
    };

    return (
        <div className="search-page">

            {/* Header */}
            <div className="search-header">
                <div>
                    <div className="search-title-row">
                        <div className="search-title-icon">
                            <Sparkles size={22} />
                        </div>

                        <div>
                            <h1>AI Document Search</h1>
                            <p>
                                Search project documents using semantic understanding instead
                                of exact keyword matching.
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Search Box */}
            <div className="search-panel">
                <form onSubmit={handleSubmit} className="search-form">

                    <div className="search-input-wrapper">
                        <SearchIcon size={21} className="search-input-icon" />

                        <input
                            type="text"
                            value={query}
                            onChange={(event) => setQuery(event.target.value)}
                            placeholder="Ask about project documents..."
                            className="search-input"
                        />

                        {query && (
                            <button
                                type="button"
                                className="clear-search"
                                onClick={() => {
                                    setQuery("");
                                    setResults([]);
                                    setSearchedQuery("");
                                    setError("");
                                }}
                            >
                                ×
                            </button>
                        )}
                    </div>

                    <button
                        type="submit"
                        className="search-button"
                        disabled={loading || !query.trim()}
                    >
                        <SearchIcon size={18} />

                        {loading ? "Searching..." : "Search"}
                    </button>

                </form>

                {/* Example queries */}
                <div className="example-section">
          <span className="example-label">
            Try searching:
          </span>

                    <div className="example-list">
                        {exampleQueries.map((example) => (
                            <button
                                key={example}
                                className="example-query"
                                onClick={() => handleExampleClick(example)}
                            >
                                {example}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {/* Error */}
            {error && (
                <div className="search-error">
                    <AlertCircle size={20} />

                    <div>
                        <strong>Search failed</strong>
                        <p>{error}</p>
                    </div>
                </div>
            )}

            {/* Loading */}
            {loading && (
                <div className="search-loading">
                    <div className="loading-spinner"></div>

                    <div>
                        <strong>Searching project knowledge...</strong>
                        <p>
                            Finding the most relevant document sections.
                        </p>
                    </div>
                </div>
            )}

            {/* Results */}
            {!loading && searchedQuery && !error && (
                <div className="results-section">

                    <div className="results-header">
                        <div>
                            <h2>Search Results</h2>

                            <p>
                                Results for{" "}
                                <span className="searched-query">
                  "{searchedQuery}"
                </span>
                            </p>
                        </div>

                        <div className="result-count">
                            {results.length}{" "}
                            {results.length === 1 ? "result" : "results"}
                        </div>
                    </div>

                    {results.length === 0 ? (
                        <div className="no-results">
                            <div className="no-results-icon">
                                <SearchIcon size={26} />
                            </div>

                            <h3>No relevant documents found</h3>

                            <p>
                                Try using different words or describe the construction issue
                                in another way.
                            </p>
                        </div>
                    ) : (
                        <div className="results-list">

                            {results.map((result, index) => (
                                <div className="search-result-card" key={result.chunkId}>

                                    {/* Result number */}
                                    <div className="result-number">
                                        {index + 1}
                                    </div>

                                    <div className="result-content">

                                        {/* Document information */}
                                        <div className="result-top">

                                            <div className="document-info">
                                                <div className="document-icon">
                                                    <FileText size={18} />
                                                </div>

                                                <div>
                                                    <h3>{result.documentName}</h3>

                                                    <div className="document-meta">
                            <span>
                              Document #{result.documentId}
                            </span>

                                                        <span>•</span>

                                                        <span>
                              Chunk {result.chunkIndex + 1}
                            </span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="semantic-badge">
                                                <Sparkles size={14} />
                                                Semantic Match
                                            </div>

                                        </div>

                                        {/* Content */}
                                        <div className="result-text">
                                            {result.content}
                                        </div>

                                        {/* Footer */}
                                        <div className="result-footer">
                      <span>
                        Chunk ID: {result.chunkId}
                      </span>

                                            <span>
                        Source: Project Documents
                      </span>
                                        </div>

                                    </div>
                                </div>
                            ))}

                        </div>
                    )}

                </div>
            )}

            {/* Empty state */}
            {!loading && !searchedQuery && !error && (
                <div className="search-empty-state">

                    <div className="empty-search-icon">
                        <Sparkles size={34} />
                    </div>

                    <h2>Search your project knowledge</h2>

                    <p>
                        Ask questions or describe a construction requirement, conflict,
                        specification, or coordination concern. AI will find relevant
                        sections across the project's processed documents.
                    </p>

                    <div className="search-capabilities">

                        <div className="capability-card">
                            <SearchIcon size={20} />
                            <div>
                                <strong>Semantic Search</strong>
                                <span>
                  Understands meaning instead of relying only on keywords.
                </span>
                            </div>
                        </div>

                        <div className="capability-card">
                            <FileText size={20} />
                            <div>
                                <strong>Document Intelligence</strong>
                                <span>
                  Searches across processed project documents and chunks.
                </span>
                            </div>
                        </div>

                        <div className="capability-card">
                            <Sparkles size={20} />
                            <div>
                                <strong>AI Ready</strong>
                                <span>
                  Provides the retrieval layer for future RAG-based answers.
                </span>
                            </div>
                        </div>

                    </div>

                </div>
            )}

        </div>
    );
}

export default Search;