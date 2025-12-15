# NightSky

NightSky is a Java-based astronomy application that allows users to view and explore the night sky. It displays celestial objects such as stars, planets, the Moon, and the Sun, along with asterisms. The application uses various astronomical coordinate systems and projections to accurately represent the sky from a given location and time.

## Features

- **Celestial Objects**: Displays stars from the HYG database, planets, the Moon, and the Sun.
- **Asterisms**: Loads and displays constellations and other star groupings.
- **Coordinate Systems**: Supports equatorial, horizontal, ecliptic, and Cartesian coordinates.
- **Projections**: Uses stereographic projection for sky rendering.
- **Interactive GUI**: Built with JavaFX, includes controls for date/time, observer location, and viewing parameters.
- **Time Animation**: Allows accelerating time to see the movement of celestial objects.
- **Design Tool**: Includes a design interface for customizing sky views.

## Project Structure

- `src/design/`: Contains the design tool GUI components, including FXML layouts and controllers.
- `src/rigel/`: Main application code.
  - `astronomy/`: Classes for celestial objects, catalogs, and astronomical calculations.
  - `coordinates/`: Coordinate system conversions and projections.
  - `gui/`: JavaFX GUI components for the main application.
  - `math/`: Mathematical utilities like angles, intervals, and polynomials.
- `resources/`: Data files including star catalog (hygdata_v3.csv), asterisms (asterisms.txt), and other datasets.
- `test/`: Unit tests.

## Requirements

- Java 17 or later
- JavaFX 17.0.13 (included in the classpath via NetBeans configuration)

## Building

This project uses Apache Ant for building. Ensure Ant is installed on your system.

1. Clone or download the project.
2. Navigate to the project root directory.
3. Run the following command to build the JAR:

   ```
   ant clean jar
   ```

   This will create `dist/NightSky.jar`.

## Running

After building, run the application with:

```
java -jar dist/NightSky.jar
```

Alternatively, run directly from the source using your IDE (e.g., NetBeans) or with JavaFX modules.

## Usage

- Launch the application to open the main sky viewer.
- Use the controls to set your observer location, date/time, and viewing parameters.
- The sky canvas displays celestial objects; zoom and pan as needed.
- Use the time accelerator to animate the sky over time.

The design tool can be launched separately if configured.

## Data Sources

- Star data: HYG Database (hygdata_v3.csv)
- Asterisms: Custom asterisms file (asterisms.txt)
- Other: Geographic data and color information

## Contributing

This project appears to be a educational or personal astronomy tool. For contributions, ensure code follows Java best practices and includes appropriate tests.

## Author

Sam Zamani
sam.zmn99@gmail.com

## License

This project is licensed under a custom license. Anyone may use this software provided they cite the author (Sam Zamani) in their projects and request permission from the author before use.

For permissions or inquiries, please contact the author
