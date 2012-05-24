/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.evaluation;

import java.util.HashMap;
import java.util.List;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.util.CriterionUri;
import eu.scape_project.planning.model.values.Value;

/**
 * This is the interface for all evaluation plugins that are providing
 * measurements that require looking at a specific object or the experiment
 * produced by applying an action to a specific object.
 * These will usually belong to the categories <ul>
 * <li>outcome object and</li>
 * <li>action runtime.</li>
 * </ul>
 * The interface defines constant URIs for core properties that are implemented 
 * and integrated in Plato.
 * @author cb
 */
public interface IObjectEvaluator extends IEvaluator {
    /**
     * relative filesize  (measure, positive number)
     */
    String OBJECT_FORMAT_RELATIVEFILESIZE   = "outcome://object/relativeFileSize";
    /**
     * is the format wellformed, valid, conforms ? (measure, boolean)
     */
    String OBJECT_FORMAT_CORRECT_WELLFORMED = "outcome://object/format/wellformed";
    String OBJECT_FORMAT_CORRECT_VALID      = "outcome://object/format/valid";
    String OBJECT_FORMAT_CORRECT_CONFORMS   = "outcome://object/format/conforms";

    /**
     * compression scheme (measure, free text)
     * compression scheme#equal (derived measure, boolean) 
     */
    String OBJECT_COMPRESSION_SCHEME   = "outcome://object/image/metadata/exif/ifd0/compression";
    String OBJECT_COMPRESSION_SCHEME_RETAINED  = "outcome://object/image/metadata/exif/ifd0/compression#retained";

    /**
     * none/lossless/lossy
     */
    String OBJECT_SUSTAINABLILITY_TRANSPARENCY_COMPRESSION = "outcome://object/compression";
    /**
     * is this sample stored lossless, or with a lossy compression? (measure, boolean)
     */
    
//    String OBJECT_COMPRESSION_LOSSLESS = "outcome://object/compression/lossless";
//    String OBJECT_COMPRESSION_LOSSY    = "outcome://object/compression/lossy";
    
    /**
     * width and height of the image (derived measure: #equal, boolean)
     */
    String OBJECT_IMAGE_DIMENSION_WIDTH          = "outcome://object/image/width";
    String OBJECT_IMAGE_DIMENSION_WIDTH_EQUAL = "outcome://object/image/width#equal";
    String OBJECT_IMAGE_DIMENSION_HEIGHT          = "outcome://object/image/height";
    String OBJECT_IMAGE_DIMENSION_HEIGHT_EQUAL = "outcome://object/image/height#equal";

    String OBJECT_IMAGE_METADATA_EXIF_IMAGEWIDTH           = "outcome://object/image/metadata/exif/ifd0/imageWidth";
    String OBJECT_IMAGE_METADATA_EXIF_IMAGEWIDTH_RETAINED  = "outcome://object/image/metadata/exif/ifd0/imageWidth#retained";
    
    String OBJECT_IMAGE_METADATA_EXIF_IDF0_IMAGEHEIGHT              = "outcome://object/image/metadata/exif/ifd0/imageHeight";
    String OBJECT_IMAGE_DIMENSION_HEIGHT_IDF0_IMAGEHEIGHT_RETAINED  = "outcome://object/image/metadata/exif/ifd0/imageHeight#retained";
    
        
    /**
     * aspect ratio of image  height/ width  (derived measure: #equal, boolean)
     */
    String OBJECT_IMAGE_DIMENSION_ASPECTRATIO = "outcome://object/image/aspectRatio";
    String OBJECT_IMAGE_DIMENSION_ASPECTRATIO_RETAINED = "outcome://object/image/aspectRatio#retained";
    
    /**
     * bits per sample (derived measure: #equal, boolean)
     * - more derived with XCL
     */
    String OBJECT_IMAGE_COLORENCODING_BITSPERSAMPLE   = "outcome://object/image/colorEncoding/bitsPerSample";
    String OBJECT_IMAGE_COLORENCODING_BITSPERSAMPLE_EQUAL   = "outcome://object/image/colorEncoding/bitsPerSample#equal";
    
    /**
     * samplesPerPixel (derived measure: #equal, boolean)
     * - more derived with XCL
     */
    String OBJECT_IMAGE_COLORENCODING_SAMPLESPERPIXEL = "outcome://object/image/colorEncoding/samplesPerPixel";
    String OBJECT_IMAGE_COLORENCODING_SAMPLESPERPIXEL_EQUAL = "outcome://object/image/colorEncoding/samplesPerPixel#equal";
    String OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORSPACE =  "outcome://object/image/photometricInterpretation/colorSpace";

    String OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORPROFILE_ICCPROFILE =  "outcome://object/image/iccProfile";
    String OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORPROFILE_ICCPROFILE_EQUAL =  "outcome://object/image/iccProfile#equal";

    String OBJECT_IMAGE_SPATIALMETRICS_SAMPLINGFREQUENCYUNIT   = "outcome://object/image/samplingFrequencyUnit";
    String OBJECT_IMAGE_SPATIALMETRICS_SAMPLINGFREQUENCYUNIT_EQUAL   = "outcome://object/image/samplingFrequencyUnit#equal";
    String OBJECT_IMAGE_SPATIALMETRICS_XSAMPLINGFREQUENCY   = "outcome://object/image/xSamplingFrequency";
    String OBJECT_IMAGE_SPATIALMETRICS_XSAMPLINGFREQUENCY_EQUAL   = "outcome://object/image/xSamplingFrequency#equal";
    String OBJECT_IMAGE_SPATIALMETRICS_YSAMPLINGFREQUENCY   = "outcome://object/image/ySamplingFrequency";
    String OBJECT_IMAGE_SPATIALMETRICS_YSAMPLINGFREQUENCY_EQUAL   = "outcome://object/image/ySamplingFrequency#equal";

    String OBJECT_IMAGE_METADATA = "outcome://object/image/metadata";
    // exif: artist
    String OBJECT_IMAGE_METADATA_PRODUCER = "outcome://object/image/metadata/producer";
    String OBJECT_IMAGE_METADATA_PRODUCER_RETAINED = "outcome://object/image/metadata/producer#retained";
    String OBJECT_IMAGE_METADATA_SOFTWARE = "outcome://object/image/metadata/software";
    String OBJECT_IMAGE_METADATA_SOFTWARE_RETAINED = "outcome://object/image/metadata/software#retained";
    // exif: DateTime
    String OBJECT_IMAGE_METADATA_CREATIONDATE = "outcome://object/image/metadata/creationDateTime";
    String OBJECT_IMAGE_METADATA_CREATIONDATE_RETAINED = "outcome://object/image/metadata/creationDateTime#retained";
    // exif: FileModifyDate/ModifyDate
    String OBJECT_IMAGE_METADATA_LASTMODIFIED = "outcome://object/image/metadata/lastModified";
    String OBJECT_IMAGE_METADATA_DESCRIPTION = "outcome://object/image/metadata/description";
    String OBJECT_IMAGE_METADATA_ORIENTATION = "outcome://object/image/metadata/orientation";
    String OBJECT_IMAGE_METADATA_ORIENTATION_RETAINED = "outcome://object/image/metadata/orientation#retained";
        
    String OBJECT_IMAGE_SIMILARITY_EQUAL = "outcome://object/image/similarity#equal";
    String OBJECT_IMAGE_SIMILARITY_AE = "outcome://object/image/similarity#ae";
    String OBJECT_IMAGE_SIMILARITY_PAE = "outcome://object/image/similarity#pae";
    String OBJECT_IMAGE_SIMILARITY_PSNR = "outcome://object/image/similarity#psnr";
    String OBJECT_IMAGE_SIMILARITY_MAE = "outcome://object/image/similarity#mae";
    String OBJECT_IMAGE_SIMILARITY_MSE = "outcome://object/image/similarity#mse";
    String OBJECT_IMAGE_SIMILARITY_RMSE = "outcome://object/image/similarity#rmse";
    String OBJECT_IMAGE_SIMILARITY_MEPP = "outcome://object/image/similarity#mepp";
    String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLE = "outcome://object/image/similarity#ssimSimple";
    String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLEHUE = "outcome://object/image/similarity#ssimHue";
    String OBJECT_IMAGE_SIMILARITY_SSIMSIMPLESATURATION = "outcome://object/image/similarity#ssimSimpleSaturation";
    String OBJECT_IMAGE_SIMILARITY_EQUALJUDGED = "outcome://object/image/similarity#equalJudged";
    
    /**
     * 
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERSAMPLE = "action://performanceEfficiency/timeBehaviour/timePerSample";
    
    /**
     * time per MB is defined msec/MB (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERMB =  "action://performanceEfficiency/timeBehaviour/timePerMB";
    
    /**
     * throughput is defined in MB per second (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_THROUGHPUT =  "action://performanceEfficiency/timeBehaviour/throughput";

    /**
     * Memory per MB (of the sample object's size) is defined (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERMB =  "action://performanceEfficiency/resourceUtilization/memoryPerMB";
    
    /**
     * Memory per Sample (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERSAMPLE =  "action://performanceEfficiency/resourceUtilization/memoryPerSample";

    /**
     * Memory peak (measure, positive number)
     */
    // TODO: not evaluated yet
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PEAK =  "action://runtime/performance/memory/peak";
    
    
    String OBJECT_ACTION_ACTIVITYLOGGING_FORMAT =  "action://functionalSuitability/functionalCompleteness/generic/activityLoggingFormat";
    String OBJECT_ACTION_ACTIVITYLOGGING_AMOUNT =  "action://functionalSuitability/functionalCompleteness/generic/activityLoggingAmount";
    
    
    /**
     * evaluates result and sample object with regard to the given critera defined in leaves
     * returns a list of values, one per leaf
     * 
     * It is not nice that leaves are passed to the evaluator, and a map of leaves to values is returned
     * 
     * This information is really needed:
     *  - how this criterion is measured (Criterion)
     *  - what is type of the evaluated value (Scale)
     *  
     * @param alternative
     * @param sample
     * @param result
     * @param criterionUris
     * @param listener
     * @return
     * @throws EvaluatorException
     */
    public HashMap<CriterionUri, Value> evaluate(
            Alternative alternative,
            SampleObject sample,
            DigitalObject result,
            List<CriterionUri> criterionUris, IStatusListener listener) throws EvaluatorException;
    
}
