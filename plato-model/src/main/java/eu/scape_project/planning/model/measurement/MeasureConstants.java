package eu.scape_project.planning.model.measurement;

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
    public static final String X_SAMPLING_FREQUENCY_PRESERVED = "http://scape-project.eu/pw/vocab/measures/59";
    public static final String Y_SAMPLING_FREQUENCY_PRESERVED = "http://scape-project.eu/pw/vocab/measures/60";
    public static final String EXIF_ALL_METADATA_RETAINED = "http://scape-project.eu/pw/vocab/measures/289";
    public static final String PRODUCER_METADATA_ELEMENT_RETAINED = "http://scape-project.eu/pw/vocab/measures/296";
    public static final String SOFTWARE_METADATA_ELEMENT_RETAINED = "http://scape-project.eu/pw/vocab/measures/297";
    public static final String DATE_AND_TIME_OF_CREATION_METADATA_ELEMENT_RETAINED = "http://scape-project.eu/pw/vocab/measures/292";
    // exif: FileModifyDate/ModifyDate
//    public static final String OBJECT_IMAGE_METADATA_LASTMODIFIED = "outcome://object/image/metadata/lastModified";
    public static final String EXIF_IMAGE_DESCRIPTION_RETAINED = "http://scape-project.eu/pw/vocab/measures/268";
    public static final String IMAGE_ORIENTATION_METADATA_ELEMENT_RETAINED = "http://scape-project.eu/pw/vocab/measures/295";
    public static final String IMAGE_CONTENT_IS_EQUAL = "http://scape-project.eu/pw/vocab/measures/2";
    public static final String ABSOLUTE_ERROR_AE = "http://scape-project.eu/pw/vocab/measures/4";
    public static final String PEAK_ABSOLUTE_ERROR_PAE = "http://scape-project.eu/pw/vocab/measures/7";
    public static final String PEAK_SIGNAL_TO_NOISE_RATIO = "http://scape-project.eu/pw/vocab/measures/8";
    public static final String MEAN_ABSOLUTE_ERROR = "http://scape-project.eu/pw/vocab/measures/5";
    public static final String MEAN_SQUARED_ERROR = "http://scape-project.eu/pw/vocab/measures/6";
    public static final String IMAGE_DISTANCE_RMSE = "http://scape-project.eu/pw/vocab/measures/3";
//    public static final String OBJECT_IMAGE_SIMILARITY_MEPP = "outcome://object/image/similarity#mepp";
    public static final String IMAGE_DISTANCE_SSIM = "http://scape-project.eu/pw/vocab/measures/1";
//    public static final String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLEHUE = "outcome://object/image/similarity#ssimHue";
//    public static final String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLESATURATION = "outcome://object/image/similarity#ssimSimpleSaturation";
//    public static final String OBJECT_IMAGE_SIMILARITY_EQUALJUDGED = "outcome://object/image/similarity#equalJudged";
    /**
	 * 
	 */
    public static final String ELAPSED_TIME_PER_OBJECT = "http://scape-project.eu/pw/vocab/measures/11";
    /**
     * time per MB is defined msec/MB (measure, positive number)
     */
    public static final String ELAPSED_TIME_PER_MB = "http://scape-project.eu/pw/vocab/measures/10";
    
    public static final String CPU_TIME_PER_MB = "http://scape-project.eu/pw/vocab/measures/12";
    
//    /**
//     * throughput is defined in MB per second (measure, positive number)
//     */
//    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_THROUGHPUT = "action://performanceEfficiency/timeBehaviour/throughput";
    /**
     * Memory per MB (of the sample object's size) is defined (measure, positive
     * number)
     */
    public static final String AVERAGE_MEMORY_USED_PER_MB = "http://scape-project.eu/pw/vocab/measures/97";

//    /**
//     * Memory per Sample (measure, positive number)
//     */
//    public static final String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERSAMPLE = "action://performanceEfficiency/resourceUtilization/memoryPerSample";

    public static final String FORMAT_OF_LOGGIN = "http://scape-project.eu/pw/vocab/measures/38";
    public static final String AMOUNT_OF_LOGGING = "http://scape-project.eu/pw/vocab/measures/37";

    public static final String NUMBER_OF_TOOLS = "http://scape-project.eu/pw/vocab/measures/141";
    public static final String NUMBER_OF_FREE_TOOLS_THAT_ARE_OPEN_SOURCE = "http://scape-project.eu/pw/vocab/measures/139";
//    public static final String FORMAT_NUMBEROFTOOLS_SAVE = "outcome://format/numberOfTools/save";
//    public static final String FORMAT_NUMBEROFTOOLS_OTHERS = "outcome://format/numberOfTools/others";

    public static final String FORMAT_COMPLEXITY = "http://scape-project.eu/pw/vocab/measures/143";

//    /**
//     * is the format open? (measure, free text)
//     * 
//     * atm:
//     * http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/disclosure/
//     * ../comment
//     */
//    public static final String FORMAT_DISCLOSURE = "outcome://format/disclosure";

    public static final String FORMAT_UBIQUITY = "http://scape-project.eu/pw/vocab/measures/162";

    public static final String FORMAT_STABILITY = "http://scape-project.eu/pw/vocab/measures/160";

    public static final String FORMAT_DOCUMENTATION_QUALITY = "http://scape-project.eu/pw/vocab/measures/148";

    public static final String FORMAT_IPR_PROTECTION = "http://scape-project.eu/pw/vocab/measures/159";

//    /**
//     * rights (measure, free text)
//     * 
//     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights
//     * 
//     */
//    public static final String FORMAT_LICENSE = "outcome://format/license";

    public static final String LICENCING_SCHEMA = "http://scape-project.eu/pw/vocab/measures/31";

//    /**
//     * which type of license applies to the tool? (measure, free text)
//     * 
//     * atm: PCDL /License
//     */
//    public static final String ACTION_LICENSE = "action://business/licence";

    public static final String BATCHPROCESSING_SUPPORTED = "http://scape-project.eu/pw/vocab/measures/33";

    public static final String RETAIN_ORIGINAL_FILENAME = "http://scape-project.eu/pw/vocab/measures/66";
}