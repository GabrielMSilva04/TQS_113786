## 1. What metrics are contributing the most to the frontend perceived performance? What do they mean?

### Key Metrics from the Report

#### First Contentful Paint (FCP): 2.9s

What it means: This is the time it takes for the browser to render the first piece of content (e.g., text, image, or canvas) on the screen. A high FCP value indicates that users are waiting too long to see any visible content.

Impact: A slow FCP negatively affects the user's perception of how quickly the site is loading.

#### Largest Contentful Paint (LCP): 3.2s

What it means: This measures the time it takes for the largest visible content (e.g., a hero image or heading text) to load and become visible. It is a key metric for user-perceived load speed.

Impact: A slow LCP can make the site feel sluggish, as users perceive the page as incomplete until the largest element is visible.

#### Speed Index: 2.9s

What it means: This measures how quickly the content is visually displayed during page load. It considers the progression of visible content over time.

Impact: A lower Speed Index indicates that the page loads content progressively and quickly, improving the user's experience.

#### Total Blocking Time (TBT): 50ms

What it means: This measures the time during which the main thread is blocked and unable to respond to user input (e.g., clicks or scrolling). 

Impact: A low TBT ensures the site remains responsive during loading.

#### Cumulative Layout Shift (CLS): 0

What it means: This measures the visual stability of the page. A score of 0 means there are no unexpected layout shifts (e.g., elements moving around as the page loads).

Impact: A stable layout improves the user experience, especially on mobile devices.

## 2. How would you make the site more accessible?

Accessibility Score: 95

- Add ARIA Labels for screen readers.

- Improve Color Contrast.

- Add tabindex attributes for Keyboard Navigation.

- Alt Text for Images.

- Form Accessibility

- Responsive Design