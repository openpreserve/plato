package eu.scape_project.planning.evaluation;

public class MeasureConstants {

    public static final String COMPARATIVE_FILE_SIZE = "http://scape-project.eu/pw/vocab/measures/123";
    /**
     * is the format wellformed, valid, conforms ? (measure, boolean)
     */
    public static final String FORMAT_CONFORMITY_WELL_FORMEDNESS = "http://scape-project.eu/pw/vocab/measures/121";
    public static final String FORMAT_CONFORMITY_VALIDITY = "http://scape-project.eu/pw/vocab/measures/120";
    public static final String FORMAT_CONFORMITY_CONFORMS = "http://scape-project.eu/pw/vocab/measures/122";

    public static final String COMPRESSION_ALGORITHM = "http://scape-project.eu/pw/vocab/measures/118";
    public static final String COMPRESSION_COMPRESSION_TYPE = "http://scape-project.eu/pw/vocab/measures/117";
    /**
     * width and height of the image (derived measure: #equal, boolean)
     */
    public static final String IMAGE_SIZE_IMAGE_WIDTH_IN_PIXELS = "http://scape-project.eu/pw/vocab/measures/50";
    public static final String IMAGE_SIZE_IMAGE_WIDTH_EQUAL = "http://scape-project.eu/pw/vocab/measures/51";
    public static final String IMAGE_SIZE_IMAGE_HEIGHT_IN_PIXELS = "http://scape-project.eu/pw/vocab/measures/52";
    public static final String IMAGE_SIZE_IMAGE_HEIGHT_EQUAL = "http://scape-project.eu/pw/vocab/measures/53";
    // String OBJECT_IMAGE_METADATA_EXIF_IMAGEWIDTH =
    // "outcome://object/image/metadata/exif/ifd0/imageWidth";
    public static final String EXIF_IMAGE_WIDTH_RETAINED = "http://scape-project.eu/pw/vocab/measures/293";
    // String OBJECT_IMAGE_METADATA_EXIF_IDF0_IMAGEHEIGHT =
    // "outcome://object/image/metadata/exif/ifd0/imageHeight";
    public static final String EXIF_IMAGE_HEIGHT_RETAINED = "http://scape-project.eu/pw/vocab/measures/294";

    public static final String IMAGE_ASPECT_RATIO_RETAINED = "http://scape-project.eu/pw/vocab/measures/9";

    public static final String COLOR_DEPTH_BITS_PER_SAMPLE_EQUAL = "http://scape-project.eu/pw/vocab/measures/169";

    // String OBJECT_IMAGE_COLORENCODING_SAMPLESPERPIXEL_EQUAL =
    // "http://scape-project.eu/pw/vocab/measures/68";
    public static final String COLOUR_MODEL_RETAINED = "http://scape-project.eu/pw/vocab/measures/68";
    public static final String SAMPLING_FREQUENCY_UNIT = "http://scape-project.eu/pw/vocab/measures/61";
    public static final String OBJECT_IMAGE_SPATIALMETRICS_XSAMPLINGFREQUENCY_EQUAL = "outcome://object/image/xSamplingFrequency#equal";
    public static final String OBJECT_IMAGE_SPATIALMETRICS_YSAMPLINGFREQUENCY_EQUAL = "outcome://object/image/ySamplingFrequency#equal";
    public static final String OBJECT_IMAGE_METADATA = "outcome://object/image/metadata";
    public static final String OBJECT_IMAGE_METADATA_PRODUCER_RETAINED = "outcome://object/image/metadata/producer#retained";
    public static final String OBJECT_IMAGE_METADATA_SOFTWARE_RETAINED = "outcome://object/image/metadata/software#retained";
    public static final String OBJECT_IMAGE_METADATA_CREATIONDATE_RETAINED = "outcome://object/image/metadata/creationDateTime#retained";
    // exif: FileModifyDate/ModifyDate
    public static final String OBJECT_IMAGE_METADATA_LASTMODIFIED = "outcome://object/image/metadata/lastModified";
    public static final String OBJECT_IMAGE_METADATA_DESCRIPTION = "outcome://object/image/metadata/description";
    public static final String OBJECT_IMAGE_METADATA_ORIENTATION_RETAINED = "outcome://object/image/metadata/orientation#retained";
    public static final String OBJECT_IMAGE_SIMILARITY_EQUAL = "outcome://object/image/similarity#equal";
    public static final String OBJECT_IMAGE_SIMILARITY_AE = "outcome://object/image/similarity#ae";
    public static final String OBJECT_IMAGE_SIMILARITY_PAE = "outcome://object/image/similarity#pae";
    public static final String OBJECT_IMAGE_SIMILARITY_PSNR = "outcome://object/image/similarity#psnr";
    public static final String OBJECT_IMAGE_SIMILARITY_MAE = "outcome://object/image/similarity#mae";
    public static final String OBJECT_IMAGE_SIMILARITY_MSE = "outcome://object/image/similarity#mse";
    public static final String OBJECT_IMAGE_SIMILARITY_RMSE = "outcome://object/image/similarity#rmse";
    public static final String OBJECT_IMAGE_SIMILARITY_MEPP = "outcome://object/image/similarity#mepp";
    public static final String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLE = "outcome://object/image/similarity#ssimSimple";
    public static final String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLEHUE = "outcome://object/image/similarity#ssimHue";
    public static final String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLESATURATION = "outcome://object/image/similarity#ssimSimpleSaturation";
    public static final String OBJECT_IMAGE_SIMILARITY_EQUALJUDGED = "outcome://object/image/similarity#equalJudged";
    /**
	 * 
	 */
    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERSAMPLE = "action://performanceEfficiency/timeBehaviour/timePerSample";
    /**
     * time per MB is defined msec/MB (measure, positive number)
     */
    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERMB = "action://performanceEfficiency/timeBehaviour/timePerMB";
    /**
     * throughput is defined in MB per second (measure, positive number)
     */
    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_THROUGHPUT = "action://performanceEfficiency/timeBehaviour/throughput";
    /**
     * Memory per MB (of the sample object's size) is defined (measure, positive
     * number)
     */
    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERMB = "action://performanceEfficiency/resourceUtilization/memoryPerMB";
    /**
     * Memory per Sample (measure, positive number)
     */
    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERSAMPLE = "action://performanceEfficiency/resourceUtilization/memoryPerSample";
    /**
     * Memory peak (measure, positive number)
     */
    public static final String OBJECT_ACTION_ACTIVITYLOGGING_FORMAT = "action://functionalSuitability/functionalCompleteness/generic/activityLoggingFormat";
    public static final String OBJECT_ACTION_ACTIVITYLOGGING_AMOUNT = "action://functionalSuitability/functionalCompleteness/generic/activityLoggingAmount";

    /**
     * how many tools are there for the given format ? (measure, positive
     * integer)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink (../Open/
     * /Save/
     */
    public static final String FORMAT_NUMBEROFTOOLS = "outcome://format/sustainability/adaption/toolSupport/nrOfTools";
    public static final String FORMAT_NUMBEROFTOOLS_OPEN = "outcome://format/numberOfTools/open";
    public static final String FORMAT_NUMBEROFTOOLS_SAVE = "outcome://format/numberOfTools/save";
    public static final String FORMAT_NUMBEROFTOOLS_OTHERS = "outcome://format/numberOfTools/others";

    /**
     * is the format complex, simple ? (measure, free text)
     * 
     * atm:
     * http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/complexity/
     * ../comment
     */
    public static final String FORMAT_COMPLEXITY = "outcome://format/complexity";

    /**
     * is the format open? (measure, free text)
     * 
     * atm:
     * http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/disclosure/
     * ../comment
     */
    public static final String FORMAT_DISCLOSURE = "outcome://format/disclosure";

    /**
     * is the format used widely, ..? (measure, free text)
     * 
     * atm:
     * http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/ubiquity/..
     * /comment
     */
    public static final String FORMAT_UBIQUITY = "outcome://format/ubiquity";

    /**
     * how stable is the format (measure, free text)
     * 
     * atm:
     * http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/stability/.
     * ./comment
     */
    public static final String FORMAT_STABILITY = "outcome://format/stability";

    /**
     * what is the quality of the format's documentation (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/
     * documentation_quality/../comment
     */
    public static final String FORMAT_DOCUMENTATION_QUALITY = "outcome://format/documentation/quality";

    /**
     * open/ipr_protected/proprietary
     */
    public static final String FORMAT_SUSTAINABILITY_RIGHTS = "outcome://format/sustainability/rights";

    /**
     * rights (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights
     * 
     */
    public static final String FORMAT_LICENSE = "outcome://format/license";

    /**
     * is it an open source tool? (measure, ordinal)
     */
    public static final String ACTION_BUSINESS_LICENCING_SCHEMA = "action://business/licencingSchema";

    /**
     * which type of license applies to the tool? (measure, free text)
     * 
     * atm: PCDL /License
     */
    public static final String ACTION_LICENSE = "action://business/licence";

    /**
     * quality is there batch support? (measure, boolean)
     */
    public static final String ACTION_BATCH_SUPPORT = "action://compatibility/interoperability/interfaces/batchProcessingSupport";

    /**
     * is the filename retained? (measure, boolean)
     */
    public static final String ACTION_RETAIN_FILENAME = "action://functionalSuitability/functionalCompleteness/generic/retainFilename";
}