# Example Dockerfile for a React frontend
FROM node:18

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install --force

# Copy source files
COPY . .


EXPOSE 3000
CMD ["npm", "run", "dev"]
