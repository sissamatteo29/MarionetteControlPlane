#!/bin/bash

# Test script for the service rediscovery endpoint

API_BASE_URL="http://localhost:8080/api"

echo "🧪 Testing Service Rediscovery Endpoint"
echo "======================================"

echo "📡 Sending rediscovery request..."
curl -X POST \
  -H "Content-Type: application/json" \
  -v \
  "${API_BASE_URL}/services/discover?fullRefresh=true"

echo -e "\n\n🔍 Checking services after rediscovery..."
curl -X GET \
  -H "Accept: application/json" \
  "${API_BASE_URL}/services" | jq .

echo -e "\n✅ Test completed!"
