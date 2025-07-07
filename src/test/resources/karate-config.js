function fn() {
  var config = {};
  // si no viene por systemProperty, usar un default (no lo necesitas realmente)
  config.baseUrl = karate.properties['baseUrl'] || 'http://localhost:8080';
  return config;
}