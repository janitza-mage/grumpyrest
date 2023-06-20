package name.martingeisse.grumpyrest.servlet;

import jakarta.servlet.http.HttpServletRequest;
import name.martingeisse.grumpyrest.path.PathUtil;

/**
 * This object defines how to obtain the request path from the servlet request. It is needed because different
 * servlet containers treat the path differently, and it can make a difference whether a servlet or a servlet filter
 * is used.
 * <p>
 * The resulting path will have no leading or trailing slash. The empty path gets returned as an empty string.
 */
public enum RequestPathSourcingStrategy {

    STARTING_WITH_CONTEXT_PATH {
        @Override
        public String getPath(HttpServletRequest request) {
            return handleResult(mergeParts(mergeParts(getContextPath(request), getServletPath(request)), getPathInfo(request)));
        }
    },
    STARTING_WITH_SERVLET_PATH {
        @Override
        public String getPath(HttpServletRequest request) {
            return handleResult(mergeParts(getServletPath(request), getPathInfo(request)));
        }
    },
    PATH_INFO_ONLY {
        @Override
        public String getPath(HttpServletRequest request) {
            return handleResult(getPathInfo(request));
        }
    };

    public abstract String getPath(HttpServletRequest request);

    private static String getContextPath(HttpServletRequest request) {
        return preparePart(request.getContextPath());
    }

    private static String getServletPath(HttpServletRequest request) {
        return preparePart(request.getServletPath());
    }

    private static String getPathInfo(HttpServletRequest request) {
        return preparePart(request.getPathInfo());
    }

    private static String preparePart(String part) {
        if (part == null) {
            return null;
        }
        part = PathUtil.trimSlashes(part);
        return part.isEmpty() ? null : part;
    }

    private static String mergeParts(String a, String b) {
        return a == null ? b : b == null ? a : (a + '/' + b);
    }

    private static String handleResult(String result) {
        return result == null ? "" : result;
    }

}
