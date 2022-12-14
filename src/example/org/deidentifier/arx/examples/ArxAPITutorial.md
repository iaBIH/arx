# ARX API Tutorial:

## Notes:

- Some functionalities are only available via GUI not API e.g. reading or creating projects.
  - Suggestion: Keep the GUI separated and move all non related fucntionalities to the main. 

## Terminologies:
 - Critiron, privacy model: same meaning. The output anonymized data must meet the defined privacy model. 
 - Transformation, Transformed data, output data: The transformation transforms the input data using e.g. generalization hierarchies, suppression, and agreggation to produce the transformed anonymized data. The output anonymized data is the selected anonymized transformed data. 
 - Metric, quality model: same meaning. This the metric that decides the best transformation. 
 - Solution space: All available transformations e.g. represented by the lattice tree.
 - Algorithm: this is the algorithm that search the solution space and find the best transformation e.g. lightning, flash or genitic.
 - Framework: is the Arx pipline.  
 - Risk: risk package has different methods to evaluate the risk of the anonymized output data.
 - Arx certificate: A short report about the anonymization process and the result in pdf format. 

## Folder Structure in Arx packages (TODO):
 - [arx](): contain the main classes e.g. Anonymizer, Configueration, Lattice, Result, Classification and Solver configuerations, ProcessStatistics, all data related classes, and Attribute class. 
   - Suggestion: Move all data related classes to an external repository that provides useful data I/O and processing functions. 
 - [arx.aggregate](): All classes related to aggregation, classification hierarchies, and statistics.
 - [arx.aggregate.classification](): All supported classiciation methods. Currently Linear regression, Naive Basian, and Random forests. More about this can be found [here]().
 - [arx.aggregate.quality](): All classes related to data quality.
 - arx.algorithm: All classes related to solution space search algorithms. Currently: Flash (optimal), Lightning, Genetic, and ed-DP. 
 - arx.certificate: All classes related to generating Arx certificate.
 - arx.certificate.elements
 - arx.certificate.resources
 - arx.common: Classes for mapping, wrapping, and grouping.
 - arx.criteria: privacy model classes. Note K-anonymity is implemented in the framework. 
 - arx.dp: classes related to Differntial Privacy. 
 - arx.exceptions: All Arx customized exceptions. 
 - arx.framwork: 
 - arx.framwork.check
 - arx.framwork.distribution
 - arx.framwork.groupify
 - arx.framwork.history
 - arx.framwork.transformer
 - arx.framwork.data: Why we have a nother Data class?
 - arx.framwork.lattice: It uses an external lattice library to generate the solution space. 
 - arx.io: All input output classes e.g. from csv, excel or dtabases. 
 - arx.metric: All classes related to data quality. 
 - arx.metric.v2
 - arx.reliablity
 - arx.risk: All classes related to risk models.
 - arx.risk.resources.us

**Tips for eclipse (TODO):**
 - Open and build Arx from source code.
 - Use Arx as library. 
 - Using formatter.
 - Tracing abstract function.
 - Show function calls hierarcy
 - Navigating backward/forward
 - Show file in project tree.
 - Reset windows.
 - Debugging.
 - Class outlines. 
 - Java doc for current file
 - Check problems
 - Commit changes to a new branch.


 ## API Howtos:
- [Create a data and hierarchies manually](#create-a-data-and-hierarchies-manually)
- [Import or read data and hierarchies from csv files](#import-or-read-data-and-hierarchies-from-csv-files)
- [Change Attribute data type e.g. to date](#change-attribute-data-type-eg-to-date)
- [Change Attribute data type format e.g. to dd.MM.yyyy](#change-attribute-data-type-format-eg-to-ddmmyyyy)
- [Change Attribute type e.g. to sensitive or quasi-identifier](#change-attribute-data-type-format-eg-to-ddmmyyyy)
- [Export or write data and hierarchies to csv files](#export-or-write-data-and-hierarchies-to-csv-files)
- [Create anonymization configueration](#create-anonymization-configueration)
- [Change anonymization configueration metric](#)
- [Start anonymization process](#start-anonymization-process)
- [Print input and output data and research subset](#print-input-and-output-data)
- [Create subset research data manually](#create-subset-research-data-manually)
- [Create subset research data using simple selector (query)](#create-subset-research-data-using-simple-selector-query)
- [Create subset research data using simple selector (query)](#create-subset-research-data-using-comblix-selector-query)


### Create a data and hierarchies manually

        import org.deidentifier.arx.Data.DefaultData;
        import org.deidentifier.arx.AttributeType.Hierarchy;
        import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;

        DefaultData data = Data.create();
        data.add("age", "gender", "zipcode");
        data.add("34", "male", "81667");
        data.add("45", "female", "81675");
        data.add("66", "male", "81925");
        data.add("70", "female", "81931");
        data.add("34", "female", "81931");
        data.add("70", "male", "81931");
        data.add("45", "male", "81931");

        // Define hierarchies
        DefaultHierarchy ageHierarchy = Hierarchy.create();
        ageHierarchy.add("34", "<50", "*");
        ageHierarchy.add("45", "<50", "*");
        ageHierarchy.add("66", ">=50", "*");
        ageHierarchy.add("70", ">=50", "*");

        DefaultHierarchy genderHierarchy = Hierarchy.create();
        genderHierarchy.add("male", "*");
        genderHierarchy.add("female", "*");

        DefaultHierarchy zipcodeHierarchy = Hierarchy.create();
        zipcodeHierarchy.add("81667", "8166*", "816**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81675", "8167*", "816**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81925", "8192*", "819**", "81***", "8****", "*****");
        zipcodeHierarchy.add("81931", "8193*", "819**", "81***", "8****", "*****");
        
        // Add hierarchies to data definition, this will change attribute type to quasi-idnetifier 
        data.getDefinition().setAttributeType("age", ageHierarchy);
        data.getDefinition().setAttributeType("gender", genderHierarchy);
        data.getDefinition().setAttributeType("zipcode", zipcodeHierarchy);


Related examples: [Example1]()

[Go to contents](#api-howtos)

### Import or read data and hierarchies from csv files

        import org.deidentifier.arx.AttributeType.Hierarchy;
        import org.deidentifier.arx.Data;

        // import data from csv file
        Data data = Data.create("data/test.csv", StandardCharsets.UTF_8, ';');
        
        // Define hierarchies from csv files
        // the attribute types will be QUASI_IDENTIFYING_ATTRIBUTE
        data.getDefinition().setAttributeType("age", Hierarchy.create("data/test_hierarchy_age.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("gender", Hierarchy.create("data/test_hierarchy_gender.csv", StandardCharsets.UTF_8, ';'));
        data.getDefinition().setAttributeType("zipcode", Hierarchy.create("data/test_hierarchy_zipcode.csv", StandardCharsets.UTF_8, ';'));


Related examples: [Example2]()

[Go to contents](#api-howtos)

### Change Attribute data type e.g. to date


Related examples: [Example3]()

[Go to contents](#api-howtos)

### Change Attribute data type format e.g. to dd.MM.yyyy

Related examples: [Example1]()

[Go to contents](#api-howtos)

### Change Attribute type e.g. to sensitive or quasi-identifier

Attribute types: 

* IDENTIFYING_ATTRIBUTE: these attributes are associated with a high risk of re-identification. They will be removed from the dataset. Typical examples are names or Social Security Numbers.
* QUASI_IDENTIFYING_ATTRIBUTE: can in combination be used for re-identification attacks. They will be transformed. Typical examples are gender, date of birth and ZIP codes.
* SENSITIVE_ATTRIBUTE: these attributes encode properties with which individuals are not willing to be linked with. As such, they might be of interest to an attacker and, if disclosed, could cause harm to data subjects. They will be kept unmodified but may be subject to further constraints, such as t-closeness or l-diversity. Typical examples are diagnoses.
* INSENSITIVE_ATTRIBUTE: these attributes are not associated with privacy risks. They will be kept unmodified.

To change or check the attribute type of "age" attribute use this code: 

        // The default type is null:
        // If hierarchy is added then the type will be QUASI_IDENTIFYING_ATTRIBUTE  
        data.getDefinition().setAttributeType("age", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);

        // check the type
        System.out.println("age attribute type: " + data.getDefinition().getAttributeType("age"));


Related examples: [Example3]()

[Go to contents](#api-howtos)

### Export or write data and hierarchies to csv files

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create anonymization configueration

Related examples: [Example]()

[Go to contents](#api-howtos)

### Change anonymization configueration metric

Related examples: [Example]()

[Go to contents](#api-howtos)

### Start anonymization process

Related examples: [Example]()

[Go to contents](#api-howtos)

### Print input and output data

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data manually

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data using simple selector (query)

Related examples: [Example]()

[Go to contents](#api-howtos)

### Create subset research data using comblix selector (query)

Related examples: [Example]()

[Go to contents](#api-howtos)

