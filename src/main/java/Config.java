public class Config {
    private String productionStatusTablePath;
    private String productionStatusTableDatabaseName;

    private String productionStatusTableStatusCollectionName;
    private String productionStatusTableDataCollectionName;
    private String assemblyStatusTablePath;
    private String assemblyStatusTableDatabaseName;
    private String assemblyStatusTableStatusCollectionName;
    private String assemblyStatusTableDataCollectionName;
    private String mongodbUri;

    public String getProductionStatusTableStatusCollectionName() {
        return productionStatusTableStatusCollectionName;
    }

    public void setProductionStatusTableStatusCollectionName(String productionStatusTableStatusCollectionName) {
        this.productionStatusTableStatusCollectionName = productionStatusTableStatusCollectionName;
    }

    public String getAssemblyStatusTableStatusCollectionName() {
        return assemblyStatusTableStatusCollectionName;
    }

    public void setAssemblyStatusTableStatusCollectionName(String assemblyStatusTableStatusCollectionName) {
        this.assemblyStatusTableStatusCollectionName = assemblyStatusTableStatusCollectionName;
    }

    public String getProductionStatusTableDatabaseName() {
        return productionStatusTableDatabaseName;
    }

    public void setProductionStatusTableDatabaseName(String productionStatusTableDatabaseName) {
        this.productionStatusTableDatabaseName = productionStatusTableDatabaseName;
    }

    public String getProductionStatusTableDataCollectionName() {
        return productionStatusTableDataCollectionName;
    }

    public void setProductionStatusTableDataCollectionName(String productionStatusTableDataCollectionName) {
        this.productionStatusTableDataCollectionName = productionStatusTableDataCollectionName;
    }

    public String getAssemblyStatusTableDatabaseName() {
        return assemblyStatusTableDatabaseName;
    }

    public void setAssemblyStatusTableDatabaseName(String assemblyStatusTableDatabaseName) {
        this.assemblyStatusTableDatabaseName = assemblyStatusTableDatabaseName;
    }

    public String getAssemblyStatusTableDataCollectionName() {
        return assemblyStatusTableDataCollectionName;
    }

    public void setAssemblyStatusTableDataCollectionName(String assemblyStatusTableDataCollectionName) {
        this.assemblyStatusTableDataCollectionName = assemblyStatusTableDataCollectionName;
    }

    public String getProductionStatusTablePath() {
        return productionStatusTablePath;
    }

    public void setProductionStatusTablePath(String productionStatusTablePath) {
        this.productionStatusTablePath = productionStatusTablePath;
    }

    public String getAssemblyStatusTablePath() {
        return assemblyStatusTablePath;
    }

    public void setAssemblyStatusTablePath(String assemblyStatusTablePath) {
        this.assemblyStatusTablePath = assemblyStatusTablePath;
    }

    public String getMongodbUri() {
        return mongodbUri;
    }

    public void setMongodbUri(String mongodbUri) {
        this.mongodbUri = mongodbUri;
    }
}
