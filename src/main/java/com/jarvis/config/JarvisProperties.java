package com.jarvis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jarvis 总配置项，绑定 application.yml 中 jarvis.* 前缀的配置。
 * 口语化说明：这就是 Spring 帮我们把 yml 文件里的内容"对号入座"到 Java 对象的工具，
 * 之后所有代码都从这个对象读配置，避免 @Value 散落各处。
 */
@ConfigurationProperties(prefix = "jarvis")
public class JarvisProperties {

    /** 用户数据根目录：嵌入式 PG 数据、评估结果、日志都放这里 */
    private String dataDir = "./jarvis-data";

    private final Ai ai = new Ai();
    private final Ingest ingest = new Ingest();
    private final Retrieval retrieval = new Retrieval();
    private final Auth auth = new Auth();

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public Ai getAi() {
        return ai;
    }

    public Ingest getIngest() {
        return ingest;
    }

    public Retrieval getRetrieval() {
        return retrieval;
    }

    public Auth getAuth() {
        return auth;
    }

    /**
     * AI 接入配置：全部指向用户自行配置的 OpenAI 兼容 API（OpenAI / DeepSeek / Ollama / LM Studio）。
     */
    public static class Ai {
        /** OpenAI 兼容 base-url，例如 https://api.openai.com/v1 或 http://localhost:11434/v1 */
        private String baseUrl = "https://api.openai.com/v1";
        /** API Key；优先从环境变量 JARVIS_AI_API_KEY 读取，避免明文落盘 */
        private String apiKey = "";
        private String chatModel = "gpt-4o-mini";
        private String embeddingModel = "text-embedding-3-small";
        /** 必须与 embedding 模型实际输出维度一致，启动时校验 */
        private int embeddingDimension = 1536;
        private int requestTimeoutSeconds = 30;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            // 环境变量优先级高于 yml，与文档"环境变量 > application.yml > 默认值"一致
            String env = System.getenv("JARVIS_AI_API_KEY");
            return (env != null && !env.isBlank()) ? env : apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getChatModel() {
            return chatModel;
        }

        public void setChatModel(String chatModel) {
            this.chatModel = chatModel;
        }

        public String getEmbeddingModel() {
            return embeddingModel;
        }

        public void setEmbeddingModel(String embeddingModel) {
            this.embeddingModel = embeddingModel;
        }

        public int getEmbeddingDimension() {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(int embeddingDimension) {
            this.embeddingDimension = embeddingDimension;
        }

        public int getRequestTimeoutSeconds() {
            return requestTimeoutSeconds;
        }

        public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
            this.requestTimeoutSeconds = requestTimeoutSeconds;
        }
    }

    /**
     * 摄入配置：文件切分粒度与支持的扩展名。
     */
    public static class Ingest {
        /** 每个 chunk（文本块）的目标字符数 */
        private int chunkSize = 300;
        /** 相邻 chunk 之间重叠的字符数，保证语义不断档 */
        private int chunkOverlap = 50;
        private java.util.List<String> supportedExtensions = java.util.List.of(".md", ".txt");

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
        }

        public int getChunkOverlap() {
            return chunkOverlap;
        }

        public void setChunkOverlap(int chunkOverlap) {
            this.chunkOverlap = chunkOverlap;
        }

        public java.util.List<String> getSupportedExtensions() {
            return supportedExtensions;
        }

        public void setSupportedExtensions(java.util.List<String> supportedExtensions) {
            this.supportedExtensions = supportedExtensions;
        }
    }

    /**
     * 检索配置：问答时取最相似的前几个片段。
     */
    public static class Retrieval {
        private int topK = 5;

        public int getTopK() {
            return topK;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }
    }

    /**
     * 本地鉴权配置：可选，防止同机其他进程误调 API。
     */
    public static class Auth {
        private String localApiKey = "";

        public String getLocalApiKey() {
            String env = System.getenv("JARVIS_LOCAL_API_KEY");
            return (env != null && !env.isBlank()) ? env : localApiKey;
        }

        public void setLocalApiKey(String localApiKey) {
            this.localApiKey = localApiKey;
        }
    }
}
