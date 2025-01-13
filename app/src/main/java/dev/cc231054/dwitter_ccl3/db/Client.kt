package dev.cc231054.dwitter_ccl3.db

import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime


const val supabaseUrl = "https://wysgyswdoefgyxubgcdl.supabase.co"
const val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind5c2d5c3dkb2VmZ3l4dWJnY2RsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY3NTg2NDgsImV4cCI6MjA1MjMzNDY0OH0.jyL9vZ-G_3bw8-W0A_7RpaCedztZKyN6nEW1qcCNgR0"
val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
    install(Auth)
    install(Realtime)
    install(Postgrest)
}